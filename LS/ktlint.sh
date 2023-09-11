./gradlew :loadKtlintReporters :runKtlintCheckOverKotlinScripts :ktlintKotlinScriptCheck :runKtlintCheckOverMainSourceSet
./gradlew :runKtlintCheckOverTestSourceSet :ktlintTestSourceSetCheck :ktlintMainSourceSetCheck
./gradlew :runKtlintFormatOverKotlinScripts :runKtlintFormatOverMainSourceSet :runKtlintFormatOverTestSourceSet
./gradlew :ktlintCheck :ktlintFormat clean build
