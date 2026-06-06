pipeline {
    agent any

    parameters {
        choice(name: 'TEST_SUITE', choices: ['api_test', 'ui_test', 'mobile_test', 'test'], description: 'Gradle test task to run')
        choice(name: 'WEB_BROWSER', choices: ['chrome', 'firefox'], description: 'Web browser for UI tests')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run web UI tests in headless browser')
        string(name: 'BROWSER_SIZE', defaultValue: '1920x1080', description: 'Browser window size for UI tests')
        string(name: 'BROWSER_VERSION', defaultValue: '', description: 'Browser version for remote UI runs; leave empty for default')
        string(name: 'REMOTE_URL', defaultValue: '', description: 'Remote WebDriver URL for Selenoid; leave empty for local browser')
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
                        runGradleTests(commonArgs())
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                publishReports()
                notifyTelegram(currentBuild.currentResult)
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

def commonArgs() {
    List args = [
            "-Dbrowser=${params.WEB_BROWSER}",
            "-Dheadless=${params.HEADLESS}",
            "-DbrowserSize=${params.BROWSER_SIZE}",
            "-DbrowserVersion=${params.BROWSER_VERSION}",
            "-DremoteUrl=${params.REMOTE_URL}",
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
            commonArgs(),
            "-DuserName=${env.BROWSERSTACK_USER}",
            "-DaccessKey=${env.BROWSERSTACK_KEY}",
            "-DbrowserstackApp=${params.BROWSERSTACK_APP}"
    ]

    return args.join(' ')
}

def publishReports() {
    junit allowEmptyResults: true, testResults: 'build/test-results/**/*.xml'

    allure([
            includeProperties: false,
            jdk              : '',
            properties       : [],
            reportBuildPolicy: 'ALWAYS',
            results          : [[path: env.ALLURE_RESULTS]]
    ])

    archiveArtifacts allowEmptyArchive: true,
            artifacts: 'build/allure-results/**/*, build/reports/tests/**/*, docs/assets/screenshots/**/*, docs/assets/video/**/*'
}

def notifyTelegram(String status) {
    try {
        withCredentials([
                string(credentialsId: 'katy-telegram-bot-token', variable: 'TELEGRAM_BOT_TOKEN'),
                string(credentialsId: 'katy-telegram-chat-id', variable: 'TELEGRAM_CHAT_ID')
        ]) {
            String message = """${env.PROJECT_NAME}
Build: #${env.BUILD_NUMBER}
Status: ${status}
Suite: ${params.TEST_SUITE}
Browser: ${params.WEB_BROWSER}
Device host: ${params.DEVICE_HOST}
Report: ${env.BUILD_URL}allure
Job: ${env.BUILD_URL}"""

            withEnv(["TELEGRAM_MESSAGE=${message}"]) {
                if (isUnix()) {
                    sh '''
                        curl -s -X POST "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/sendMessage" \
                        -d chat_id="$TELEGRAM_CHAT_ID" \
                        --data-urlencode text="$TELEGRAM_MESSAGE"
                    '''
                } else {
                    powershell '''
                        $body = @{
                            chat_id = "$env:TELEGRAM_CHAT_ID"
                            text = "$env:TELEGRAM_MESSAGE"
                        }
                        Invoke-RestMethod -Uri "https://api.telegram.org/bot$env:TELEGRAM_BOT_TOKEN/sendMessage" -Method Post -Body $body
                    '''
                }
            }
        }
    } catch (Exception ignored) {
        echo 'Telegram credentials are not configured. Skipping Telegram notification.'
    }
}
