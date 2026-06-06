# Jenkins, Allure and Telegram setup

This project contains a ready Jenkins pipeline in `Jenkinsfile`.

## Required Jenkins plugins

- Git
- JUnit
- Allure Jenkins Plugin
- Credentials Binding

## Jenkins credentials

Create these credentials in Jenkins before running the job.

| Credentials ID | Type | Used for |
| --- | --- | --- |
| `telegram-bot-token` | Secret text | Telegram bot token |
| `telegram-chat-id` | Secret text | Telegram chat id |
| `browserstack-username` | Secret text | BrowserStack username |
| `browserstack-access-key` | Secret text | BrowserStack access key |

BrowserStack credentials are needed only for `mobile_test` with `DEVICE_HOST=browserstack`.

## Pipeline parameters

| Parameter | Example | Description |
| --- | --- | --- |
| `TEST_SUITE` | `api_test`, `ui_test`, `mobile_test`, `test` | Gradle task to run |
| `DEVICE_HOST` | `emulator`, `browserstack` | Mobile execution host |
| `DEVICE_NAME` | `Pixel_7` | Android device name |
| `PLATFORM_VERSION` | `11` | Android version |
| `BROWSERSTACK_APP` | `bs://...` | Uploaded BrowserStack app id |
| `HEADLESS` | `true` | Headless mode for web UI tests |

## Reports

The pipeline publishes:

- JUnit XML results from `build/test-results`.
- Allure report from `build/allure-results`.
- Archived Allure raw results and local docs assets.
- Telegram message with build status and Allure report link.

## UI video

UI tests can attach video to Allure when they run in Selenoid or another remote browser grid with video recording.

Required parameters:

```bash
-DremoteUrl=https://selenoid.autotests.cloud/wd/hub
-DenableVideo=true
-DvideoStorageUrl=https://selenoid.autotests.cloud/video/
```

The test base enables Selenoid capabilities `enableVNC` and `enableVideo`, then attaches an HTML video player to the Allure test result.

## Mobile evidence

Mobile tests automatically attach:

- Emulator or BrowserStack screenshot.
- Page source.
- BrowserStack video when tests are executed with `DEVICE_HOST=browserstack`.

For local emulator runs, prepare the Open Food Facts app once before the Jenkins
demo run: install the APK, complete onboarding, then run with `-DnoReset=true`.
This keeps product smoke tests focused on the main screen and search flow rather
than the onboarding tutorial.

## Telegram

Telegram can be configured in two ways:

- Jenkins credentials used by `Jenkinsfile`: `telegram-bot-token` and `telegram-chat-id`.
- Allure Notifications template: `notifications.json`.

Keep real Telegram tokens outside Git. If a token was committed to a public repository before, revoke it in BotFather and create a new one.

Real demo screenshots and GIF/video files can be added to:

- `docs/assets/screenshots`
- `docs/assets/video`
