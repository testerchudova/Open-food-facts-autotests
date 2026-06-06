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
| `katy-telegram-bot-token` | Secret text | Telegram bot token |
| `katy-telegram-chat-id` | Secret text | Telegram chat id |
| `katy-browserstack-username` | Secret text | BrowserStack username |
| `katy-browserstack-access-key` | Secret text | BrowserStack access key |

BrowserStack credentials are needed only for `mobile_test` with `DEVICE_HOST=browserstack`.

## Pipeline parameters

| Parameter | Example | Description |
| --- | --- | --- |
| `TEST_SUITE` | `api_test`, `ui_test`, `mobile_test`, `test` | Gradle task to run |
| `WEB_BROWSER` | `chrome`, `firefox` | Browser for web UI tests |
| `HEADLESS` | `true` | Headless mode for web UI tests |
| `BROWSER_SIZE` | `1920x1080` | Browser window size for web UI tests |
| `BROWSER_VERSION` | `100.0` | Browser version for remote UI runs; can be empty |
| `REMOTE_URL` | `https://selenoid.autotests.cloud/wd/hub` | Remote WebDriver URL for Selenoid; empty means local browser |
| `ENABLE_VIDEO` | `true` | Enables UI video attachment when `REMOTE_URL` is set |
| `VIDEO_STORAGE_URL` | `https://selenoid.autotests.cloud/video/` | Selenoid video storage URL |
| `DEVICE_HOST` | `emulator`, `browserstack` | Mobile execution host, used for `mobile_test` |
| `DEVICE_NAME` | `Pixel_7` | Android device name for `mobile_test` |
| `PLATFORM_VERSION` | `11` | Android version for `mobile_test` |
| `BROWSERSTACK_APP` | `bs://...` | Uploaded BrowserStack app id for `mobile_test` |

## Reports

The pipeline publishes:

- JUnit XML results from `build/test-results`.
- Allure report from `build/allure-results`.
- Archived Allure raw results and local docs assets.
- Telegram message with build status and Allure report link.

## UI video

UI tests can attach video to Allure when they run in Selenoid or another remote browser grid with video recording.

Required Jenkins parameters:

- `TEST_SUITE=ui_test`
- `REMOTE_URL=https://selenoid.autotests.cloud/wd/hub`
- `ENABLE_VIDEO=true`
- `VIDEO_STORAGE_URL=https://selenoid.autotests.cloud/video/`

The test base enables Selenoid capabilities `enableVNC` and `enableVideo`, then attaches an HTML video player to the Allure test result.

For a regular local web UI run, leave `REMOTE_URL` empty and use:

- `TEST_SUITE=ui_test`
- `WEB_BROWSER=chrome`
- `HEADLESS=true`

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

- Jenkins credentials used by `Jenkinsfile`: `katy-telegram-bot-token` and `katy-telegram-chat-id`.
- Allure Notifications template: `notifications.json`.

Keep real Telegram tokens outside Git. If a token was committed to a public repository before, revoke it in BotFather and create a new one.

Real demo screenshots and GIF/video files can be added to:

- `docs/assets/screenshots`
- `docs/assets/video`
