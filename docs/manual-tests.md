# Manual Test Cases

## Scope

Manual checks cover website, API behavior visible to a tester, and Android app smoke scenarios for Open Food Facts.

## Web

| ID | Title | Preconditions | Steps | Expected result | Priority |
| --- | --- | --- | --- | --- | --- |
| WEB-001 | Search product by barcode | Browser is opened | Open `https://world.openfoodfacts.org`, enter `3017620422003` in search, submit search | Search results contain the product with the requested barcode | High |
| WEB-002 | Open product card | Search results are shown | Click a product card | Product page opens and contains product name, barcode, brand and nutrition section | High |
| WEB-003 | Search by product name | Browser is opened | Search for `nutella` | Results list contains several relevant products | Medium |
| WEB-004 | Invalid search query | Browser is opened | Search for `qa-autotest-no-product-0000000000` | Website displays an empty-result message | Medium |
| WEB-005 | Product nutrition facts | Product page is opened | Scroll to nutrition information | Nutrition table or nutrition block is visible | High |

## API

| ID | Title | Preconditions | Request | Expected result | Priority |
| --- | --- | --- | --- | --- | --- |
| API-001 | Get product by barcode | Public API is available | `GET /api/v2/product/3017620422003.json` | Status code is 200, response `status` is 1, `product` is not empty | High |
| API-002 | Get unknown product | Public API is available | `GET /api/v2/product/0000000000000.json` | Status code is 200, response `status` is 0, status text says product was not found | High |
| API-003 | Search products | Public API is available | `GET /api/v2/search?search_terms=nutella&page_size=10` | Status code is 200, product list is not empty | High |
| API-004 | Page size limit | Public API is available | `GET /api/v2/search?search_terms=nutella&page_size=3` | Response contains no more than 3 products | Medium |
| API-005 | Staging product preparation | Staging credentials exist | `POST /cgi/product_jqm2.pl` with product form data | Product draft is accepted on staging | Low |

## Mobile Android

| ID | Title | Preconditions | Steps | Expected result | Priority |
| --- | --- | --- | --- | --- | --- |
| MOB-001 | First launch | Open Food Facts app is installed | Launch app | App opens without crash and shows onboarding or main screen | High |
| MOB-002 | Skip onboarding | Onboarding is shown | Tap skip/not now | Main screen opens | Medium |
| MOB-003 | Search product | App is opened | Open search, enter `Nutella` | Search results contain matching product | High |
| MOB-004 | Scanner entry point | App is opened | Find scan action on main screen | Scan action is visible and available | High |
| MOB-005 | Camera permission | App is opened and camera permission is not granted | Tap scan action | App requests camera permission or shows permission explanation | High |

## Regression Checklist

- Search works on website and mobile app.
- Product page contains identity data: barcode, name, brand.
- Product page contains nutrition data.
- API product endpoint returns correct statuses for existing and unknown barcodes.
- Allure report contains steps and attachments after automated runs.
