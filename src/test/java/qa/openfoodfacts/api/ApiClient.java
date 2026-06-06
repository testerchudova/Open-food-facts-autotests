package qa.openfoodfacts.api;

import org.aeonbits.owner.ConfigFactory;
import qa.openfoodfacts.config.ApiConfig;
import qa.openfoodfacts.config.CredentialsConfig;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.function.Supplier;

public abstract class ApiClient {

    protected final ApiConfig apiConfig = ConfigFactory.create(ApiConfig.class, System.getProperties());
    protected final CredentialsConfig credentialsConfig = ConfigFactory.create(CredentialsConfig.class, System.getProperties());

    protected <T> T withNetworkRetry(Supplier<T> action) {
        RuntimeException lastError = null;

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                return action.get();
            } catch (RuntimeException e) {
                if (!isRetriableNetworkError(e)) {
                    throw e;
                }
                lastError = e;
                sleepBeforeRetry();
            }
        }

        throw lastError;
    }

    private static boolean isRetriableNetworkError(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof SocketTimeoutException || current instanceof SocketException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static void sleepBeforeRetry() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry wait was interrupted", e);
        }
    }
}
