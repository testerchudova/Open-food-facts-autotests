# Manual setup checklist: Jenkins, Allure TestOps, Jira, Telegram

Use this checklist when configuring infrastructure manually.

## Jenkins

1. Create a new Pipeline job.
2. Connect this GitHub repository.
3. Use `Jenkinsfile` from the repository root.
4. Add parameters:
   - `TEST_SUITE`
   - `WEB_BROWSER`
   - `HEADLESS`
   - `BROWSER_SIZE`
   - `BROWSER_VERSION`
   - `REMOTE_URL`
   - `ENABLE_VIDEO`
   - `VIDEO_STORAGE_URL`
   - `DEVICE_HOST`
   - `DEVICE_NAME`
   - `PLATFORM_VERSION`
   - `BROWSERSTACK_APP`
   - `BROWSERSTACK_APP_URL`
5. Add Jenkins credentials:
   - `katy-telegram-bot-token`
   - `katy-telegram-chat-id`
   - `browserstack-credentials`
6. Install and configure Allure Jenkins Plugin.
7. Run `api_test`, `ui_test` and `mobile_test` as separate builds.
8. For BrowserStack mobile runs, either pass an existing `bs://...` value in `BROWSERSTACK_APP` or keep `BROWSERSTACK_APP` empty and set `BROWSERSTACK_APP_URL` so Jenkins uploads the APK automatically.

## UI video

For UI test video use Selenoid or another remote browser grid with video recording.

Required Jenkins parameters:

- `REMOTE_URL`: Selenoid WebDriver URL.
- `HEADLESS`: `false`.
- `ENABLE_VIDEO`: `true`.
- `VIDEO_STORAGE_URL`: public URL where Selenoid stores mp4 files.

For Jenkins UI runs, use `REMOTE_URL=https://<login>:<password>@selenoid.autotests.cloud/wd/hub`.
Clear `REMOTE_URL` only when the Jenkins agent has a local browser installed.
`api_test` and `mobile_test` do not use Selenoid.

Example:

```bash
gradle clean ui_test \
  -DremoteUrl=https://<login>:<password>@selenoid.autotests.cloud/wd/hub \
  -Dheadless=false \
  -DenableVideo=true \
  -DvideoStorageUrl=https://selenoid.autotests.cloud/video/
```

## Mobile screenshots and video

For mobile tests in Jenkins use BrowserStack App Automate.

Local Android emulator runs can be used outside Jenkins for README screenshots.

BrowserStack run requires:

- `deviceHost=browserstack`
- Jenkins credential `browserstack-credentials`
- `BROWSERSTACK_APP` or `BROWSERSTACK_APP_URL`
- `DEVICE_NAME=Google Pixel 7`
- `PLATFORM_VERSION=13.0`

Allure attachments include mobile screenshot, page source and BrowserStack video.

## Telegram

Telegram report is sent through Allure Notifications:

- Jenkins credentials: `katy-telegram-bot-token` and `katy-telegram-chat-id`.
- Template config: `notifications.json`.
- Runtime config with real secrets is created only during the Jenkins build.

Do not commit real Telegram tokens. Use Jenkins credentials or create a local non-committed copy.

## Allure TestOps

1. Create a project in Allure TestOps.
2. Connect Jenkins job to the TestOps project.
   - Server ID: `allure-server`
   - Project ID: `5238`
3. Add launch parameters in TestOps:
   - `TEST_SUITE`
   - `DEVICE_HOST`
   - `browser`
   - `browserVersion`
   - `platformVersion`
4. Upload Jenkins Pipeline results through `withAllureUpload` from `build/allure-results`.
5. Run Jenkins job from TestOps.
6. Check that test cases are created from automated tests.

## Jira

1. Create Jira task for the diploma project: `HOMEWORK-1611`.
2. Connect Jira integration in Allure TestOps.
3. Link automated test cases to the Jira task through `@Issue("HOMEWORK-1611")`.
4. Add final Jira screenshot to `docs/assets/screenshots`.

## Demo artifacts for README

After real infrastructure runs, add screenshots:

- Jenkins job with parameters.
- Allure Report overview.
- Allure test with UI video attachment.
- Allure test with mobile screenshot/video attachment.
- Telegram message.
- Allure TestOps project.
- Jira task integration.
