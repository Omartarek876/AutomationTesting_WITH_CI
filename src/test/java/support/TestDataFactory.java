package support;

/** Helpers for producing unique values from static test data. */
public final class TestDataFactory {
    private TestDataFactory() {
    }

    public static String timestampSuffix() {
        return Long.toString(System.currentTimeMillis());
    }

    public static String timestampedValue(String value, String timestamp) {
        return value + timestamp;
    }

    public static String timestampedEmail(String email, String timestamp) {
        int atIndex = email.lastIndexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            throw new IllegalArgumentException("Email must contain a local part and domain: " + email);
        }

        return email.substring(0, atIndex) + "." + timestamp + email.substring(atIndex);
    }
}
