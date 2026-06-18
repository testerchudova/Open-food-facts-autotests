package qa.openfoodfacts.helpers;

import com.codeborne.selenide.Screenshots;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class Attach {

    private Attach() {
    }

    @Attachment(value = "{attachName}", type = "image/png")
    public static byte[] screenshotAs(String attachName) {
        return ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "Page source", type = "text/html", fileExtension = ".html")
    public static byte[] pageSource() {
        return getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }

    @Attachment(value = "Browser console logs", type = "text/plain", fileExtension = ".log")
    public static String browserConsoleLogs() {
        try {
            return String.join("\n", getWebDriver().manage().logs().get(LogType.BROWSER)
                    .getAll()
                    .stream()
                    .map(Object::toString)
                    .toList());
        } catch (UnsupportedOperationException ignored) {
            return "Browser console logs are not supported by this driver";
        }
    }

    @Attachment(value = "{attachName}", type = "text/plain", fileExtension = ".txt")
    public static String textAs(String attachName, String message) {
        return message;
    }

    @Attachment(value = "BrowserStack video", type = "text/html", fileExtension = ".html")
    public static String browserStackVideo(String sessionId) {
        String videoUrl = BrowserStackHelper.videoUrl(sessionId);
        return videoHtml(videoUrl);
    }

    @Attachment(value = "Selenoid video", type = "text/html", fileExtension = ".html")
    public static String selenoidVideo(String videoUrl) {
        return videoHtml(videoUrl);
    }

    @Attachment(value = "{attachName}", type = "video/mp4", fileExtension = ".mp4")
    public static byte[] videoAsBytes(String attachName, File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static File lastScreenshot() {
        return Screenshots.getLastScreenshot();
    }

    private static String videoHtml(String videoUrl) {
        String safeVideoUrl = escapeHtml(videoUrl);
        return "<html><body>"
                + "<p><a href='" + safeVideoUrl + "' target='_blank'>Open video in a new tab</a></p>"
                + "<video width='100%' height='100%' controls>"
                + "<source src='" + safeVideoUrl + "' type='video/mp4'>"
                + "</video></body></html>";
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
