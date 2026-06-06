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
| `browserstack-credentials` | Username with password | BrowserStack username and access key |

BrowserStack credentials are needed only for `mobile_test` with `DEVICE_HOST=browserstack`.
Selenoid access is passed through the `REMOTE_URL` Jenkins parameter, for example `https://<login>:<password>@selenoid.autotests.cloud/wd/hub`.

## Pipeline parameters

| Parameter | Example | Description |
| --- | --- | --- |
| `TEST_SUITE` | `api_test`, `ui_test`, `mobile_test`, `test` | Gradle task to run |
| `WEB_BROWSER` | `chrome`, `firefox` | Browser for web UI tests |
| `HEADLESS` | `false` | Headless mode for web UI tests; keep `false` for Selenoid video |
| `BROWSER_SIZE` | `1920x1080` | Browser window size for web UI tests |
| `BROWSER_VERSION` | `100.0` | Browser version for remote UI runs; can be empty |
| `REMOTE_URL` | `https://<login>:<password>@selenoid.autotests.cloud/wd/hub` | Remote WebDriver URL for Selenoid; empty means local browser |
| `ENABLE_VIDEO` | `true` | Enables UI video attachment when `REMOTE_URL` is set |
| `VIDEO_STORAGE_URL` | `https://selenoid.autotests.cloud/video/` | Selenoid video storage URL |
| `DEVICE_HOST` | `emulator`, `browserstack` | Mobile execution host, used for `mobile_test` |
| `DEVICE_NAME` | `Pixel_7` | Android device name for `mobile_test` |
| `PLATFORM_VERSION` | `11` | Android version for `mobile_test` |
| `BROWSERSTACK_APP` | `bs://...` | Optional already uploaded BrowserStack app id for `mobile_test` with `DEVICE_HOST=browserstack` |
| `BROWSERSTACK_APP_URL` | `https://world.openfoodfacts.org/files/off.apk` | Public APK URL used for automatic BrowserStack upload when `BROWSERSTACK_APP` is empty |

## Reports

The pipeline publishes:

- JUnit XML results from `build/test-results`.
- Allure report from `build/allure-results`.
- Archived Allure raw results and local docs assets.
- Telegram message with Allure chart, build status and Allure report link.

The pipeline cleans old Allure and JUnit artifacts before every run so failed setup does not publish stale results from a previous build.

## UI video

UI tests can attach video to Allure when they run in Selenoid or another remote browser grid with video recording.

Required Jenkins parameters:

- `TEST_SUITE=ui_test`
- `REMOTE_URL=https://<login>:<password>@selenoid.autotests.cloud/wd/hub`
- `HEADLESS=false`
- `ENABLE_VIDEO=true`
- `VIDEO_STORAGE_URL=https://selenoid.autotests.cloud/video/`

The test base enables Selenoid capabilities `enableVNC` and `enableVideo`, then attaches an HTML video player to the Allure test result.

For Jenkins web UI runs, keep the default Selenoid value:

- `TEST_SUITE=ui_test`
- `WEB_BROWSER=chrome`
- `HEADLESS=false`
- `REMOTE_URL=https://<login>:<password>@selenoid.autotests.cloud/wd/hub`

If `HEADLESS=true`, the test can pass, but the Selenoid video may contain only
the Selenoid start screen instead of the browser actions.

Without Selenoid login and password in `REMOTE_URL`, Selenoid responds with `401 Authorization Required`.

For a local browser run on a machine with Chrome installed, clear `REMOTE_URL`.

## Mobile evidence

Mobile tests automatically attach:

- Emulator or BrowserStack screenshot.
- Page source.
- BrowserStack video when tests are executed with `DEVICE_HOST=browserstack`.

For local emulator runs, prepare the Open Food Facts app once before the Jenkins
demo run: install the APK, complete onboarding, then run with `-DnoReset=true`.
This keeps product smoke tests focused on the main screen and search flow rather
than the onboarding tutorial.

For BrowserStack runs, the pipeline works like the previous mobile homework
project: it prepares the app before tests and passes the resulting `bs://...`
value through Jenkins environment variables. You can either:

- set `BROWSERSTACK_APP` to an already uploaded `bs://...` app id;
- leave `BROWSERSTACK_APP` empty and keep `BROWSERSTACK_APP_URL` filled so Jenkins uploads the APK automatically.

BrowserStack username, access key and app id are not passed as plain Gradle
arguments. Tests read them from `BROWSERSTACK_USER`, `BROWSERSTACK_KEY` and
`BROWSERSTACK_APP` environment variables provided by Jenkins.

## Telegram

Telegram report is sent through `allure-notifications-4.11.0.jar` and `notifications.json`.

- `katy-telegram-bot-token` and `katy-telegram-chat-id` are stored as Jenkins credentials.
- `notifications.json` stays in Git with placeholders only.
- During the Jenkins build, `Jenkinsfile` creates a temporary runtime config and removes it after sending the report.

Keep real Telegram tokens outside Git. If a token was committed to a public repository before, revoke it in BotFather and create a new one.

Real demo screenshots and GIF/video files can be added to:

- `docs/assets/screenshots`
- `docs/assets/video`
