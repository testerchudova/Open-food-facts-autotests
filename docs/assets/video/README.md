# Video

Real video artifacts after running tests in Jenkins, Selenoid or BrowserStack.

Current files:

- `slnd.mp4` - UI test run video from Allure/Selenoid.

UI video is attached to Allure automatically when tests are executed with remote Selenoid settings:

```bash
gradle clean ui_test -DremoteUrl=https://selenoid.example.com/wd/hub -Dheadless=false -DenableVideo=true
```

Mobile video is attached to Allure automatically when tests are executed in BrowserStack:

```bash
gradle clean mobile_test -DdeviceHost=browserstack -DbrowserstackApp=bs://...
```

In Jenkins, BrowserStack username, access key and app id are passed through environment variables.
