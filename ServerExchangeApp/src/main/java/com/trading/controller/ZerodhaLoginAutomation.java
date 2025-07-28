package com.trading.controller;

import org.apache.commons.codec.binary.Base32;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ZerodhaLoginAutomation {

    // Replace these values before production use
    private static final String PASSWORD = "Diki@0102";
    private static final String USERID = "SUK704";
    private static final String API_KEY = "neu3t85b8a4a4hoo";
    private static final String TOTP_SECRET_BASE32 = "VENCVHOQHHGF6ZXXHFVFURSD3MR4LIN6";

    public static void main(String[] args) {

        // Optional: Set chromedriver manually if not defined globally
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        WebDriver driver = new ChromeDriver();
        driver.manage().deleteAllCookies();

        try {
            driver.manage().window().setSize(new Dimension(1512, 898));

            // ✅ Open Kite Connect login screen (OAuth)
            String loginUrl = "https://kite.trade/connect/login?api_key=" + API_KEY + "&v=3";
            driver.get(loginUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Step 1: Enter User ID
            WebElement useridInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userid")));
            useridInput.clear();
            useridInput.sendKeys(USERID);

            // Step 2: Enter password
            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            passwordInput.clear();
            passwordInput.sendKeys(PASSWORD);

            // Step 3: Click Login
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button-orange")));
            loginButton.click();

            // Step 4: Wait for TOTP field (same id="userid" is reused here for OTP)
            WebElement totpInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userid")));

            // Step 5: Generate and enter TOTP
            String totpCode = generateTOTPCode(TOTP_SECRET_BASE32);
            totpInput.clear();
            totpInput.sendKeys(totpCode);

            // Step 6: Find and click "Continue", fresh element after DOM reload
            WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button-orange.wide")));
            continueButton.click();

            // Optional: Wait after TOTP submit until redirection settles
            Thread.sleep(5000);

            String redirectedUrl = driver.getCurrentUrl();
            System.out.println("Redirected URL: " + redirectedUrl);

            // Step 7: Extract and print request_token if present
            String requestToken = extractRequestToken(redirectedUrl);
            System.out.println("✅ request_token: " + requestToken);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // Always quit browser
        }
    }

    /**
     * Generates current 6-digit TOTP code for Zerodha 2FA.
     */
    private static String generateTOTPCode(String base32Secret) {
        Base32 base32 = new Base32();
        byte[] secret = base32.decode(base32Secret);
        long timeWindow = Instant.now().getEpochSecond() / 30;

        try {
            byte[] data = ByteBuffer.allocate(8).putLong(timeWindow).array();
            SecretKeySpec keySpec = new SecretKeySpec(secret, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xf;

            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            int otp = binary % 1_000_000;
            return String.format("%06d", otp);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("❌ Failed to generate TOTP", e);
        }
    }

    /**
     * Extracts request_token from a redirected URL query string.
     */
    private static String extractRequestToken(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();

            if (query == null) {
                throw new RuntimeException("❌ No query parameters in redirected URL.");
            }

            Map<String, String> params = new HashMap<>();
            for (String pair : query.split("&")) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    params.put(parts[0], URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
                }
            }

            String token = params.get("request_token");
            if (token == null) {
                throw new RuntimeException("❌ request_token not found in redirected URL.");
            }
            return token;
        } catch (Exception e) {
            throw new RuntimeException("❌ Error extracting request_token", e);
        }
    }
}
