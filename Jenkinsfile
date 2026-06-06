pipeline {
    agent any

    parameters {
        choice(name: 'TEST_SUITE', choices: ['api_test', 'ui_test', 'mobile_test', 'test'], description: 'Gradle test task to run')
        choice(name: 'WEB_BROWSER', choices: ['chrome', 'firefox'], description: 'Web browser for UI tests')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run web UI tests in headless browser')
        string(name: 'BROWSER_SIZE', defaultValue: '1920x1080', description: 'Browser window size for UI tests')
        string(name: 'BROWSER_VERSION', defaultValue: '', description: 'Browser version for remote UI runs; leave empty for default')
        string(name: 'REMOTE_URL', defaultValue: 'https://selenoid.autotests.cloud/wd/hub', description: 'Remote WebDriver URL for Selenoid; clear it only for local browser')
        booleanParam(name: 'ENABLE_VIDEO', defaultValue: true, description: 'Enable UI video when REMOTE_URL is configured')
        string(name: 'VIDEO_STORAGE_URL', defaultValue: 'https://selenoid.autotests.cloud/video/', description: 'Selenoid video storage URL')
        choice(name: 'DEVICE_HOST', choices: ['emulator', 'browserstack'], description: 'Mobile execution host for mobile_test only')
        string(name: 'DEVICE_NAME', defaultValue: 'Pixel_7', description: 'Android device name for mobile_test')
        string(name: 'PLATFORM_VERSION', defaultValue: '11', description: 'Android platform version for mobile_test')
        string(name: 'BROWSERSTACK_APP', defaultValue: '', description: 'BrowserStack uploaded app id for mobile_test, for example bs://...')
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

        stage('Run tests') {
            steps {
                script {
                    if (params.TEST_SUITE == 'mobile_test' && params.DEVICE_HOST == 'browserstack') {
                        withCredentials([
                                string(credentialsId: 'katy-browserstack-username', variable: 'BROWSERSTACK_USER'),
                                string(credentialsId: 'katy-browserstack-access-key', variable: 'BROWSERSTACK_KEY')
                        ]) {
                            runGradleTests(browserStackArgs())
                        }
                    } else {
                        runGradleTests(commonArgs(params.REMOTE_URL))
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
            rm -rf build/allure-results build/test-results build/reports/tests allure-report notifications-runtime.json
            mkdir -p build/allure-results
        '''
    } else {
        powershell '''
            Remove-Item "build/allure-results", "build/test-results", "build/reports/tests", "allure-report", "notifications-runtime.json" -Recurse -Force -ErrorAction SilentlyContinue
            New-Item -ItemType Directory -Force "build/allure-results" | Out-Null
        '''
    }
}

def commonArgs(String remoteUrl) {
    List args = [
            "-Dbrowser=${params.WEB_BROWSER}",
            "-Dheadless=${params.HEADLESS}",
            "-DbrowserSize=${params.BROWSER_SIZE}",
            "-DbrowserVersion=${params.BROWSER_VERSION}",
            "-DremoteUrl=${remoteUrl}",
            "-DenableVideo=${params.ENABLE_VIDEO}",
            "-DvideoStorageUrl=${params.VIDEO_STORAGE_URL}",
            "-DdeviceHost=${params.DEVICE_HOST}",
            "-DdeviceName=${params.DEVICE_NAME}",
            "-DplatformVersion=${params.PLATFORM_VERSION}"
    ]

    return args.join(' ')
}

def browserStackArgs() {
    List args = [
            commonArgs(params.REMOTE_URL),
            "-DuserName=${env.BROWSERSTACK_USER}",
            "-DaccessKey=${env.BROWSERSTACK_KEY}",
            "-DbrowserstackApp=${params.BROWSERSTACK_APP}"
    ]

    return args.join(' ')
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

            java -DconfigFile="${configFile}" -jar "\$JAR_NAME"
        """
    } else {
        powershell """
            \$ErrorActionPreference = "Stop"
            \$jarName = "allure-notifications-4.11.0.jar"

            if (-not (Test-Path \$jarName)) {
                Invoke-WebRequest -Uri "https://github.com/qa-guru/allure-notifications/releases/download/4.11.0/\$jarName" -OutFile \$jarName
            }

            java "-DconfigFile=${configFile}" -jar \$jarName
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
        return params.DEVICE_HOST == 'browserstack' ? 'BrowserStack' : 'Android Emulator'
    }

    if (params.TEST_SUITE == 'ui_test') {
        return params.REMOTE_URL?.trim() ? 'Selenoid' : "Local ${params.WEB_BROWSER}"
    }

    if (params.TEST_SUITE == 'api_test') {
        return 'API / Jenkins'
    }

    return 'Jenkins'
}
