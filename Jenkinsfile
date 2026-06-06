pipeline {
    agent any

    parameters {
        choice(name: 'TEST_SUITE', choices: ['api_test', 'ui_test', 'mobile_test', 'test'], description: 'Gradle test task to run')
        choice(name: 'DEVICE_HOST', choices: ['emulator', 'browserstack'], description: 'Mobile execution host')
        string(name: 'DEVICE_NAME', defaultValue: 'Pixel_7', description: 'Android device name')
        string(name: 'PLATFORM_VERSION', defaultValue: '11', description: 'Android platform version')
        string(name: 'BROWSERSTACK_APP', defaultValue: '', description: 'BrowserStack uploaded app id, for example bs://...')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run web UI tests in headless browser')
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
                                string(credentialsId: 'browserstack-username', variable: 'BROWSERSTACK_USER'),
                                string(credentialsId: 'browserstack-access-key', variable: 'BROWSERSTACK_KEY')
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
            "-Dheadless=${params.HEADLESS}",
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
                string(credentialsId: 'telegram-bot-token', variable: 'TELEGRAM_BOT_TOKEN'),
                string(credentialsId: 'telegram-chat-id', variable: 'TELEGRAM_CHAT_ID')
        ]) {
            String message = """${env.PROJECT_NAME}
Build: #${env.BUILD_NUMBER}
Status: ${status}
Suite: ${params.TEST_SUITE}
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
