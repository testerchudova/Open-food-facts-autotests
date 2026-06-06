# Open Food Facts Autotests

<h3 align="center">Diploma QA automation project for <a href="https://world.openfoodfacts.org">world.openfoodfacts.org</a></h3>

<p align="center">
  <a href="https://www.java.com/"><img alt="Java" src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white"></a>
  <a href="https://gradle.org/"><img alt="Gradle" src="https://img.shields.io/badge/Gradle-8.10-02303A?logo=gradle&logoColor=white"></a>
  <a href="https://junit.org/junit5/"><img alt="JUnit 5" src="https://img.shields.io/badge/JUnit_5-tests-25A162?logo=junit5&logoColor=white"></a>
  <a href="https://selenide.org/"><img alt="Selenide" src="https://img.shields.io/badge/Selenide-UI-43B02A"></a>
  <a href="https://rest-assured.io/"><img alt="Rest Assured" src="https://img.shields.io/badge/REST_Assured-API-6DB33F"></a>
  <a href="https://appium.io/"><img alt="Appium" src="https://img.shields.io/badge/Appium-Mobile-662D91?logo=appium&logoColor=white"></a>
  <a href="https://allurereport.org/"><img alt="Allure" src="https://img.shields.io/badge/Allure-Report-FF6A00"></a>
</p>

## Contents

* [Project overview](#project-overview)
* [Technology stack](#technology-stack)
* [Automated checks](#automated-checks)
* [Project structure](#project-structure)
* [Run tests](#run-tests)
* [Configuration](#configuration)
* [Allure report](#allure-report)
* [CI, reports and notifications](#ci-reports-and-notifications)
* [Manual tests](#manual-tests)
* [Useful links](#useful-links)

## Project overview

This project combines four diploma blocks around one product: Open Food Facts.

- UI autotests for the public website with Page Objects.
- API autotests with clients, Lombok models, request/response specifications and custom Allure REST Assured templates.
- API data preparation for UI scenarios: tests receive stable product data through API before checking it in UI.
- Android mobile autotests with Appium/Selenide and screen objects.
- Manual test cases stored as project documentation.

## Technology stack

| Area | Tools |
| --- | --- |
| Language and build | Java 17, Gradle |
| Test framework | JUnit 5 |
| UI automation | Selenide |
| API automation | REST Assured, Jackson, Lombok |
| Mobile automation | Appium Java Client, Selenide |
| Configuration | Owner |
| Assertions | AssertJ |
| Reporting | Allure Report, Allure Selenide, Allure REST Assured |
| CI and notifications | Jenkins, Allure TestOps, Jira, Telegram, BrowserStack, Selenoid |

## Automated checks

### UI

- Main page search input is visible.
- Product page displays barcode and brand.
- Product page contains nutrition information.
- Search scenarios are implemented, but disabled by default because Open Food Facts can rate-limit repeated anonymous search requests.

### API

- Product can be received by barcode.
- Product response contains brand and nutriments.
- Invalid barcode validation check is implemented, but disabled by default because the public endpoint can timeout on invalid barcode requests.
- Search endpoint tests are implemented through `SearchApiClient`, but disabled by default because Open Food Facts can rate-limit anonymous search requests.
- API prepares product data for UI tests.
- Staging product draft creation is implemented and disabled until credentials are provided.

### Mobile

- Android app main screen contains scan action.
- Android app submits search for product data prepared through API.

Mobile tests are enabled only for `mobile_test` or when `-DrunMobile=true` is passed.

## Project structure

```text
src/test/java/qa/openfoodfacts
├── api
│   ├── ApiClient.java
│   ├── AuthApiClient.java
│   ├── ProductsApiClient.java
│   └── SearchApiClient.java
├── config
├── data
├── drivers
├── helpers
├── mobile
│   ├── MainScreen.java
│   ├── ProductScreen.java
│   └── SearchScreen.java
├── models
│   ├── products
│   ├── search
│   └── users
├── pages
│   ├── MainPage.java
│   ├── ProductPage.java
│   └── SearchPage.java
├── specs
└── tests
    ├── api
    ├── mobile
    └── web
```

## Run tests

Run all non-mobile tests:

```bash
gradle clean test
```

Run UI tests:

```bash
gradle clean ui_test
```

Run UI tests with Selenoid video:

```bash
gradle clean ui_test \
  -DremoteUrl=https://selenoid.autotests.cloud/wd/hub \
  -DenableVideo=true \
  -DvideoStorageUrl=https://selenoid.autotests.cloud/video/
```

Run API tests:

```bash
gradle clean api_test
```

Run Android mobile tests:

```bash
gradle clean mobile_test
```

For a local emulator the recommended APK is the official F-Droid build saved as
`src/test/resources/apps/openfoodfacts-fdroid.apk`. APK files are ignored by Git,
so download or place the app locally before running mobile tests.

The local emulator should have onboarding completed once before the stable smoke
suite is launched. The project uses `noReset=true` so Appium keeps that prepared
application state between sessions.

Run mobile tests locally with custom Appium parameters:

```bash
gradle clean mobile_test \
  -DdeviceHost=emulator \
  -DlocalUrl=http://127.0.0.1:4723/wd/hub \
  -DdeviceName=Pixel_4 \
  -Dudid=emulator-5556 \
  -DplatformVersion=17 \
  -Dapp=src/test/resources/apps/openfoodfacts-fdroid.apk \
  -DnoReset=true
```

Run mobile tests in BrowserStack:

```bash
gradle clean mobile_test \
  -DdeviceHost=browserstack \
  -DuserName=${BROWSERSTACK_USER} \
  -DaccessKey=${BROWSERSTACK_KEY} \
  -DbrowserstackApp=${BROWSERSTACK_APP_ID}
```

## Configuration

Default configuration files are stored in `src/test/resources/config`.

| File | Purpose |
| --- | --- |
| `api.properties` | API base URL, user agent, default product data |
| `web.properties` | Browser, base URL, timeout, remote URL, Selenoid video settings |
| `mobile.properties` | Appium, emulator and BrowserStack settings |
| `credentials.example.properties` | Template for staging credentials |

Create `src/test/resources/config/credentials.properties` from the example when you need staging authorization. This file is ignored by Git.

## Allure report

Generate report after a test run:

```bash
gradle allureReport
```

Open report locally:

```bash
gradle allureServe
```

The project attaches:

- UI screenshots.
- Page source.
- Browser console logs.
- UI video from Selenoid for remote web runs.
- Mobile screenshots and BrowserStack video.
- API request and response logs with custom templates from `src/test/resources/tpl`.

## CI, reports and notifications

The project contains infrastructure templates and documentation:

- `Jenkinsfile` runs selected Gradle suites, publishes JUnit and Allure reports, archives artifacts and sends Telegram notifications.
- `notifications.json` is a template for Allure Notifications Telegram messages.
- [docs/jenkins.md](docs/jenkins.md) describes Jenkins credentials, parameters, reports and mobile evidence.
- [docs/testops-jira-checklist.md](docs/testops-jira-checklist.md) contains a manual checklist for Jenkins, Allure TestOps, Jira and Telegram setup.
- [docs/assets/screenshots](docs/assets/screenshots) is reserved for real Jenkins, Allure, TestOps, Jira, Telegram and emulator screenshots.
- [docs/assets/video](docs/assets/video) is reserved for real UI and mobile run videos.

Real tokens and chat ids must be stored in Jenkins credentials, not in Git.

## Manual tests

Manual checks are stored in [docs/manual-tests.md](docs/manual-tests.md).

## Useful links

- Product under test: [Open Food Facts](https://world.openfoodfacts.org)
- Official API docs: [Open Food Facts API](https://openfoodfacts.github.io/openfoodfacts-server/api/)
- Android app repository: [openfoodfacts/smooth-app](https://github.com/openfoodfacts/smooth-app)
- Latest checked Android release: `v4.23.0`, published on January 6, 2026
