# Video

Add real video artifacts here after running tests in Jenkins, Selenoid or BrowserStack.

Recommended files:

- `ui-selenoid-run.mp4`
- `mobile-browserstack-run.mp4`
- `demo-report.gif`

UI video is attached to Allure automatically when tests are executed with remote Selenoid settings:

```bash
gradle clean ui_test -DremoteUrl=https://selenoid.example.com/wd/hub -DenableVideo=true
```

Mobile video is attached to Allure automatically when tests are executed in BrowserStack:

```bash
gradle clean mobile_test -DdeviceHost=browserstack -DbrowserstackApp=bs://...
```

In Jenkins, BrowserStack username, access key and app id are passed through environment variables.
