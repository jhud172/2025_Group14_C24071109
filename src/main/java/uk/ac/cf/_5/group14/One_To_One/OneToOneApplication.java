package uk.ac.cf._5.group14.One_To_One;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OneToOneApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(OneToOneApplication.class);

	public static void main(String[] args) {
		applyRenderDatabaseUrlFallback();
		var context = SpringApplication.run(OneToOneApplication.class, args);
		logStartupDatasourceAndProfiles(context.getEnvironment());
	}

	private static void logStartupDatasourceAndProfiles(Environment environment) {
		String[] activeProfiles = environment.getActiveProfiles();
		if (activeProfiles.length == 0) {
			activeProfiles = environment.getDefaultProfiles();
		}

		String datasourceUrl = environment.getProperty("spring.datasource.url");
		String datasourceSource = detectDatasourceSource();

		LOGGER.info("Active profile(s): {}", String.join(",", activeProfiles));
		LOGGER.info("Datasource URL (sanitized): {}", sanitizeDatasourceUrl(datasourceUrl));
		LOGGER.info("Datasource source: {}", datasourceSource);
	}

	private static void applyRenderDatabaseUrlFallback() {
		String rawDatabaseUrl = System.getenv("DATABASE_URL");
		if (hasText(rawDatabaseUrl)) {
			String normalizedDatabaseUrl = normalizeJdbcPostgresUrl(rawDatabaseUrl);
			System.setProperty("DATABASE_URL", normalizedDatabaseUrl);
			System.setProperty("spring.datasource.url", normalizedDatabaseUrl);
			applyDriverClassForUrl(normalizedDatabaseUrl);
			System.setProperty("app.datasource.url-origin", normalizedDatabaseUrl.equals(rawDatabaseUrl)
					? "DATABASE_URL"
					: "DATABASE_URL (normalized)");
			return;
		}

		String explicitDatasourceUrl = System.getProperty("spring.datasource.url");
		if (hasText(explicitDatasourceUrl)) {
			String normalizedExplicitUrl = normalizeJdbcPostgresUrl(explicitDatasourceUrl);
			System.setProperty("spring.datasource.url", normalizedExplicitUrl);
			applyDriverClassForUrl(normalizedExplicitUrl);
			System.setProperty("app.datasource.url-origin", normalizedExplicitUrl.equals(explicitDatasourceUrl)
					? "spring.datasource.url"
					: "spring.datasource.url (jdbc normalized)");
			return;
		}

	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private static String normalizeJdbcPostgresUrl(String jdbcUrl) {
		if (jdbcUrl.startsWith("postgres://") || jdbcUrl.startsWith("postgresql://")) {
			URI uri = URI.create(jdbcUrl);
			String normalizedJdbcUrl = "jdbc:postgresql://" + uri.getHost()
					+ (uri.getPort() > 0 ? ":" + uri.getPort() : "")
					+ uri.getPath()
					+ (hasText(uri.getQuery()) ? "?" + uri.getQuery() : "");

			String userInfo = uri.getUserInfo();
			if (hasText(userInfo) && !hasText(System.getProperty("spring.datasource.username"))
					&& !hasText(System.getenv("DATABASE_USER"))) {
				String[] userInfoParts = userInfo.split(":", 2);
				if (userInfoParts.length > 0 && hasText(userInfoParts[0])) {
					System.setProperty("spring.datasource.username", userInfoParts[0]);
				}
				if (userInfoParts.length == 2 && hasText(userInfoParts[1])
						&& !hasText(System.getProperty("spring.datasource.password"))
						&& !hasText(System.getenv("DATABASE_PASSWORD"))) {
					System.setProperty("spring.datasource.password", userInfoParts[1]);
				}
			}

			return normalizedJdbcUrl;
		}

		if (jdbcUrl.startsWith("jdbc:jdbc:postgresql://")) {
			jdbcUrl = jdbcUrl.replaceFirst("^jdbc:jdbc:postgresql://", "jdbc:postgresql://");
		}

		String prefix = "jdbc:postgresql://";
		if (!jdbcUrl.startsWith(prefix)) {
			return jdbcUrl;
		}

		String remainder = jdbcUrl.substring(prefix.length());
		if (!remainder.contains("@")) {
			return jdbcUrl;
		}

		URI uri = URI.create("postgresql://" + remainder);
		String normalizedJdbcUrl = "jdbc:postgresql://" + uri.getHost()
				+ (uri.getPort() > 0 ? ":" + uri.getPort() : "")
				+ uri.getPath()
				+ (hasText(uri.getQuery()) ? "?" + uri.getQuery() : "");

		String userInfo = uri.getUserInfo();
		if (hasText(userInfo) && !hasText(System.getProperty("spring.datasource.username"))
				&& !hasText(System.getenv("DATABASE_USER"))) {
			String[] userInfoParts = userInfo.split(":", 2);
			if (userInfoParts.length > 0 && hasText(userInfoParts[0])) {
				System.setProperty("spring.datasource.username", userInfoParts[0]);
			}
			if (userInfoParts.length == 2 && hasText(userInfoParts[1])
					&& !hasText(System.getProperty("spring.datasource.password"))
					&& !hasText(System.getenv("DATABASE_PASSWORD"))) {
				System.setProperty("spring.datasource.password", userInfoParts[1]);
			}
		}

		return normalizedJdbcUrl;
	}

	private static void applyDriverClassForUrl(String jdbcUrl) {
		if (!hasText(jdbcUrl)) {
			return;
		}

		if (jdbcUrl.startsWith("jdbc:postgresql://")) {
			System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
			return;
		}

		if (jdbcUrl.startsWith("jdbc:h2:")) {
			System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
		}
	}

	private static String detectDatasourceSource() {
		String normalizedOrigin = System.getProperty("app.datasource.url-origin");
		if (hasText(normalizedOrigin)) {
			return normalizedOrigin;
		}

		if (hasText(System.getProperty("spring.datasource.url"))) {
			return "spring.datasource.url system property";
		}
		if (hasText(System.getenv("DATABASE_URL"))) {
			return "DATABASE_URL env";
		}

		return "application properties/default";
	}

	private static String sanitizeDatasourceUrl(String datasourceUrl) {
		if (!hasText(datasourceUrl)) {
			return "(not set)";
		}

		String maskedCredentials = datasourceUrl.replaceAll("(//)([^/@]+)@", "$1***@");
		String maskedQuery = maskedCredentials.replaceAll("([?&](password|pass|pwd)=)[^&]+", "$1***");
		return maskedQuery;
	}

}
