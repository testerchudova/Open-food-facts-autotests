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
5. Add Jenkins credentials:
   - `katy-telegram-bot-token`
   - `katy-telegram-chat-id`
   - `katy-browserstack-username`
   - `katy-browserstack-access-key`
6. Install and configure Allure Jenkins Plugin.
7. Run `api_test`, `ui_test` and `mobile_test` as separate builds.

## UI video

For UI test video use Selenoid or another remote browser grid with video recording.

Required Jenkins parameters:

- `REMOTE_URL`: Selenoid WebDriver URL.
- `ENABLE_VIDEO`: `true`.
- `VIDEO_STORAGE_URL`: public URL where Selenoid stores mp4 files.

Example:

```bash
gradle clean ui_test \
  -DremoteUrl=https://selenoid.autotests.cloud/wd/hub \
  -DenableVideo=true \
  -DvideoStorageUrl=https://selenoid.autotests.cloud/video/
```

## Mobile screenshots and video

For mobile tests use either:

- local Android emulator;
- BrowserStack App Automate.

BrowserStack run requires:

- `deviceHost=browserstack`
- `userName`
- `accessKey`
- `browserstackApp`

Allure attachments include mobile screenshot, page source and BrowserStack video.

## Telegram

This project has two possible Telegram approaches:

- Jenkins pipeline notification through credentials `katy-telegram-bot-token` and `katy-telegram-chat-id`.
- Allure Notifications configuration through `notifications.json`.

Do not commit real Telegram tokens. Use Jenkins credentials or create a local non-committed copy.

## Allure TestOps

1. Create a project in Allure TestOps.
2. Connect Jenkins job to the TestOps project.
3. Add launch parameters in TestOps:
   - `TEST_SUITE`
   - `DEVICE_HOST`
   - `browser`
   - `browserVersion`
   - `platformVersion`
4. Run Jenkins job from TestOps.
5. Check that test cases are created from automated tests.

## Jira

1. Create Jira task for the diploma project.
2. Connect Jira integration in Allure TestOps.
3. Link automated test cases or launch results to the Jira task.
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
