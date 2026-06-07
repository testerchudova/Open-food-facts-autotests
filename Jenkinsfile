pipeline {
    agent any

    parameters {
        choice(name: 'TEST_SUITE', choices: ['api_test', 'ui_test', 'mobile_test', 'test'], description: 'Gradle test task to run')
        choice(name: 'WEB_BROWSER', choices: ['chrome', 'firefox'], description: 'Web browser for UI tests')
        booleanParam(name: 'HEADLESS', defaultValue: false, description: 'Run web UI tests in headless browser; keep false for Selenoid video')
        string(name: 'BROWSER_SIZE', defaultValue: '1920x1080', description: 'Browser window size for UI tests')
        string(name: 'BROWSER_VERSION', defaultValue: '', description: 'Browser version for remote UI runs; leave empty for default')
        string(name: 'REMOTE_URL', defaultValue: 'https://selenoid.autotests.cloud/wd/hub', description: 'Remote WebDriver URL for Selenoid; used for UI tests only')
        booleanParam(name: 'ENABLE_VIDEO', defaultValue: true, description: 'Enable UI video when REMOTE_URL is configured')
        string(name: 'VIDEO_STORAGE_URL', defaultValue: 'https://selenoid.autotests.cloud/video/', description: 'Selenoid video storage URL')
        choice(name: 'DEVICE_HOST', choices: ['browserstack'], description: 'Mobile execution host for mobile_test in Jenkins')
        string(name: 'DEVICE_NAME', defaultValue: 'Google Pixel 7', description: 'BrowserStack public Android device name for mobile_test')
        string(name: 'PLATFORM_VERSION', defaultValue: '13.0', description: 'BrowserStack Android platform version for mobile_test')
        string(name: 'BROWSERSTACK_APP', defaultValue: '', description: 'BrowserStack uploaded app id for mobile_test, for example bs://...')
        string(name: 'BROWSERSTACK_APP_URL', defaultValue: 'https://world.openfoodfacts.org/files/off.apk', description: 'Public APK URL to upload to BrowserStack when BROWSERSTACK_APP is empty')
    }

    environment {
        PROJECT_NAME = 'Open Food Facts autotests'
        ALLURE_RESULTS = 'build/allure-results'
    }

    stages {
        stage('Prepare') {
            steps {
                script {
                    cleanReportArtifacts()

                    if (isUnix()) {
                        sh 'chmod +x gradlew'
                    }
                }
            }
        }

        stage('Upload Android app') {
            when {
                expression { browserStackMobileRun() && !params.BROWSERSTACK_APP?.trim() }
            }
            steps {
                withCredentials([
                        usernamePassword(
                                credentialsId: 'browserstack-credentials',
                                usernameVariable: 'BROWSERSTACK_USER',
                                passwordVariable: 'BROWSERSTACK_KEY'
                        )
                ]) {
                    script {
                        env.BROWSERSTACK_APP = uploadBrowserStackApp()
                        echo 'Android app was uploaded to BrowserStack.'
                    }
                }
            }
        }

        stage('Run tests') {
            steps {
                script {
                    withAllureUpload(
                            name: "${env.JOB_NAME} - #${env.BUILD_NUMBER} - ${params.TEST_SUITE}",
                            projectId: '5238',
                            results: [[path: env.ALLURE_RESULTS]],
                            serverId: 'allure-server',
                            tags: "openfoodfacts,diploma,${params.TEST_SUITE}"
                    ) {
                        runSelectedTests()
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (publishReports()) {
                    sendTelegramReport()
                } else {
                    echo 'No Allure test results were found. Telegram report is skipped.'
                }
            }
        }
    }
}

def runSelectedTests() {
    if (browserStackMobileRun()) {
        withCredentials([
                usernamePassword(
                        credentialsId: 'browserstack-credentials',
                        usernameVariable: 'BROWSERSTACK_USER',
                        passwordVariable: 'BROWSERSTACK_KEY'
                )
        ]) {
            env.BROWSERSTACK_APP = resolveBrowserStackApp()
            runGradleTests(browserStackArgs())
        }
    } else {
        runGradleTests(commonArgs(effectiveRemoteUrl()))
    }
}

def runGradleTests(String extraArgs) {
    String command = "${gradleExecutable()} clean ${params.TEST_SUITE} ${extraArgs}".trim()

    if (isUnix()) {
        sh command
    } else {
        bat command
    }
}

def gradleExecutable() {
    return isUnix() ? './gradlew' : 'gradlew.bat'
}

def cleanReportArtifacts() {
    if (isUnix()) {
        sh '''
            rm -rf build/allure-results build/test-results build/reports/tests allure-report notifications-runtime.json allure-notifications.log
            mkdir -p build/allure-results
        '''
    } else {
        powershell '''
            Remove-Item "build/allure-results", "build/test-results", "build/reports/tests", "allure-report", "notifications-runtime.json", "allure-notifications.log" -Recurse -Force -ErrorAction SilentlyContinue
            New-Item -ItemType Directory -Force "build/allure-results" | Out-Null
        '''
    }
}

def commonArgs(String remoteUrl) {
    List args = [
            systemPropertyArg('browser', params.WEB_BROWSER),
            systemPropertyArg('headless', params.HEADLESS),
            systemPropertyArg('browserSize', params.BROWSER_SIZE),
            systemPropertyArg('browserVersion', params.BROWSER_VERSION),
            systemPropertyArg('remoteUrl', remoteUrl),
            systemPropertyArg('enableVideo', params.ENABLE_VIDEO),
            systemPropertyArg('videoStorageUrl', params.VIDEO_STORAGE_URL),
            systemPropertyArg('deviceHost', effectiveDeviceHost()),
            systemPropertyArg('deviceName', effectiveDeviceName()),
            systemPropertyArg('platformVersion', effectivePlatformVersion())
    ]

    return args.join(' ')
}

def effectiveDeviceName() {
    if (!browserStackMobileRun()) {
        return params.DEVICE_NAME
    }

    String deviceName = params.DEVICE_NAME?.trim()
    return (!deviceName || deviceName == 'Pixel_7') ? 'Google Pixel 7' : deviceName
}

def effectiveDeviceHost() {
    return params.TEST_SUITE == 'mobile_test' ? 'browserstack' : params.DEVICE_HOST
}

def effectivePlatformVersion() {
    if (!browserStackMobileRun()) {
        return params.PLATFORM_VERSION
    }

    String platformVersion = params.PLATFORM_VERSION?.trim()
    return (!platformVersion || effectiveDeviceName() == 'Google Pixel 7' && platformVersion == '11') ? '13.0' : platformVersion
}

def browserStackArgs() {
    List args = [
            commonArgs('')
    ]

    return args.join(' ')
}

def effectiveRemoteUrl() {
    return params.TEST_SUITE == 'mobile_test' ? '' : params.REMOTE_URL
}

def systemPropertyArg(String name, Object value) {
    return commandArg("-D${name}=${value == null ? '' : value.toString()}")
}

def commandArg(String value) {
    if (isUnix()) {
        return "'${value.replace("'", "'\"'\"'")}'"
    }

    String escapedValue = value.replace('"', '\\"')
    return "\"${escapedValue}\""
}

def browserStackMobileRun() {
    return params.TEST_SUITE == 'mobile_test'
}

def resolveBrowserStackApp() {
    String app = firstNonBlank(params.BROWSERSTACK_APP, env.BROWSERSTACK_APP)

    if (!app) {
        error 'BrowserStack app id is empty. Set BROWSERSTACK_APP or provide BROWSERSTACK_APP_URL for automatic upload.'
    }

    return app
}

def uploadBrowserStackApp() {
    String localApp = 'src/test/resources/apps/openfoodfacts.apk'
    String uploadResponse

    if (fileExists(localApp)) {
        uploadResponse = uploadBrowserStackAppFile(localApp)
    } else {
        String appUrl = params.BROWSERSTACK_APP_URL?.trim()

        if (!appUrl) {
            error "BrowserStack app was not prepared. Add ${localApp} to the Jenkins workspace, set BROWSERSTACK_APP, or set BROWSERSTACK_APP_URL."
        }

        uploadResponse = uploadBrowserStackAppUrl(appUrl)
    }

    String app = extractBrowserStackAppUrl(uploadResponse)

    if (!app) {
        echo "BrowserStack upload response: ${uploadResponse}"
        error 'BrowserStack upload did not return app_url.'
    }

    return app
}

def extractBrowserStackAppUrl(String uploadResponse) {
    String marker = '"app_url"'
    int keyIndex = uploadResponse.indexOf(marker)

    if (keyIndex < 0) {
        return ''
    }

    int colonIndex = uploadResponse.indexOf(':', keyIndex + marker.length())

    if (colonIndex < 0) {
        return ''
    }

    int valueStart = uploadResponse.indexOf('"', colonIndex + 1)

    if (valueStart < 0) {
        return ''
    }

    int valueEnd = uploadResponse.indexOf('"', valueStart + 1)

    if (valueEnd < 0) {
        return ''
    }

    return uploadResponse.substring(valueStart + 1, valueEnd)
}

def uploadBrowserStackAppFile(String localApp) {
    withEnv(["BROWSERSTACK_APP_FILE=${localApp}"]) {
        if (isUnix()) {
            return sh(script: '''
                set +x
                curl -sS -u "$BROWSERSTACK_USER:$BROWSERSTACK_KEY" \
                    -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
                    -F "file=@$BROWSERSTACK_APP_FILE"
            ''', returnStdout: true).trim()
        }

        return powershell(script: '''
            $ErrorActionPreference = "Stop"
            curl.exe -sS -u "${env:BROWSERSTACK_USER}:${env:BROWSERSTACK_KEY}" `
                -X POST "https://api-cloud.browserstack.com/app-automate/upload" `
                -F "file=@$env:BROWSERSTACK_APP_FILE"
        ''', returnStdout: true).trim()
    }
}

def uploadBrowserStackAppUrl(String appUrl) {
    withEnv(["BROWSERSTACK_UPLOAD_URL=${appUrl}"]) {
        if (isUnix()) {
            return sh(script: '''
                set +x
                curl -sS -u "$BROWSERSTACK_USER:$BROWSERSTACK_KEY" \
                    -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
                    -F "url=$BROWSERSTACK_UPLOAD_URL"
            ''', returnStdout: true).trim()
        }

        return powershell(script: '''
            $ErrorActionPreference = "Stop"
            curl.exe -sS -u "${env:BROWSERSTACK_USER}:${env:BROWSERSTACK_KEY}" `
                -X POST "https://api-cloud.browserstack.com/app-automate/upload" `
                -F "url=$env:BROWSERSTACK_UPLOAD_URL"
        ''', returnStdout: true).trim()
    }
}

def firstNonBlank(Object... values) {
    for (Object value : values) {
        if (value != null && value.toString().trim()) {
            return value.toString().trim()
        }
    }

    return ''
}

def publishReports() {
    junit allowEmptyResults: true, testResults: 'build/test-results/**/*.xml'

    boolean hasResults = hasAllureResults()

    if (hasResults) {
        allure([
                includeProperties: false,
                jdk              : '',
                properties       : [],
                reportBuildPolicy: 'ALWAYS',
                results          : [[path: env.ALLURE_RESULTS]]
        ])
    } else {
        echo "No files were found in ${env.ALLURE_RESULTS}. Allure report publishing is skipped."
    }

    archiveArtifacts allowEmptyArchive: true,
            artifacts: 'build/allure-results/**/*, build/reports/tests/**/*, docs/assets/screenshots/**/*, docs/assets/video/**/*'

    return hasResults
}

def hasAllureResults() {
    if (isUnix()) {
        return sh(script: "find '${env.ALLURE_RESULTS}' -type f -name '*-result.json' | grep -q .", returnStatus: true) == 0
    }

    return powershell(script: """
        \$files = Get-ChildItem '${env.ALLURE_RESULTS}' -Filter '*-result.json' -File -ErrorAction SilentlyContinue
        if (\$files.Count -gt 0) { exit 0 }
        exit 1
    """, returnStatus: true) == 0
}

def sendTelegramReport() {
    try {
        withCredentials([
                string(credentialsId: 'katy-telegram-bot-token', variable: 'TELEGRAM_BOT_TOKEN'),
                string(credentialsId: 'katy-telegram-chat-id', variable: 'TELEGRAM_CHAT_ID')
        ]) {
            withEnv(["EXECUTION_ENVIRONMENT=${executionEnvironment()}"]) {
                String configFile = 'notifications-runtime.json'

                try {
                    writeTelegramRuntimeConfig(configFile)
                    runAllureNotifications(configFile)
                } finally {
                    removeRuntimeConfig(configFile)
                }
            }
        }
    } catch (Exception error) {
        echo "Allure Telegram notification was skipped: ${error}"
    }
}

def writeTelegramRuntimeConfig(String configFile) {
    String runtimeConfig = readFile('notifications.json')
            .replace('${TELEGRAM_BOT_TOKEN}', env.TELEGRAM_BOT_TOKEN ?: '')
            .replace('${TELEGRAM_CHAT_ID}', env.TELEGRAM_CHAT_ID ?: '')
            .replace('${BUILD_URL}', env.BUILD_URL ?: '')
            .replace('${EXECUTION_ENVIRONMENT}', env.EXECUTION_ENVIRONMENT ?: executionEnvironment())

    writeFile file: configFile, text: runtimeConfig, encoding: 'UTF-8'
}

def runAllureNotifications(String configFile) {
    if (isUnix()) {
        sh """
            set +x
            JAR_NAME="allure-notifications-4.11.0.jar"

            if [ ! -f "\$JAR_NAME" ]; then
                if command -v curl >/dev/null 2>&1; then
                    curl -fsSL -o "\$JAR_NAME" "https://github.com/qa-guru/allure-notifications/releases/download/4.11.0/\$JAR_NAME"
                else
                    wget -q -O "\$JAR_NAME" "https://github.com/qa-guru/allure-notifications/releases/download/4.11.0/\$JAR_NAME"
                fi
            fi

            if java -DconfigFile="${configFile}" -jar "\$JAR_NAME" > allure-notifications.log 2>&1; then
                echo "Allure Telegram notification was sent."
            else
                sed -E 's#bot[0-9]+(%3A|:)[A-Za-z0-9_-]+#bot****#g' allure-notifications.log || true
                exit 1
            fi
        """
    } else {
        powershell """
            \$ErrorActionPreference = "Stop"
            \$jarName = "allure-notifications-4.11.0.jar"

            if (-not (Test-Path \$jarName)) {
                Invoke-WebRequest -Uri "https://github.com/qa-guru/allure-notifications/releases/download/4.11.0/\$jarName" -OutFile \$jarName
            }

            java "-DconfigFile=${configFile}" -jar \$jarName *> "allure-notifications.log"

            if (\$LASTEXITCODE -eq 0) {
                Write-Host "Allure Telegram notification was sent."
            } else {
                Get-Content "allure-notifications.log" | ForEach-Object {
                    \$_ -replace 'bot[0-9]+(%3A|:)[A-Za-z0-9_-]+', 'bot****'
                }
                exit \$LASTEXITCODE
            }
        """
    }
}

def removeRuntimeConfig(String configFile) {
    if (isUnix()) {
        sh "rm -f '${configFile}'"
    } else {
        powershell "Remove-Item '${configFile}' -Force -ErrorAction SilentlyContinue"
    }
}

def executionEnvironment() {
    if (params.TEST_SUITE == 'mobile_test') {
        return 'BrowserStack'
    }

    if (params.TEST_SUITE == 'ui_test') {
        return params.REMOTE_URL?.trim() ? 'Selenoid' : "Local ${params.WEB_BROWSER}"
    }

    if (params.TEST_SUITE == 'api_test') {
        return 'API / Jenkins'
    }

    return 'Jenkins'
}
