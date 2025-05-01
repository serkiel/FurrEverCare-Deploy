package cit.edu.furrevercare.config; // Adjust package if needed

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct; // Use jakarta if using Spring Boot 3+
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct // Initialize Firebase when this component is created
    public void initializeFirebase() {
        try {
            // === REMOVE lines that load serviceAccountKey.json from classpath ===
            // Example of line to remove:
            // InputStream serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();

            // === USE THIS INSTEAD: ===
            // Let the SDK automatically find credentials based on the environment
            // (It checks GOOGLE_APPLICATION_CREDENTIALS environment variable first)
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                // Explicitly setting Project ID is good practice (get from your service account JSON)
                .setProjectId("furrevercare-fe125")
                .build();

            // Initialize only if no default app exists yet (prevents errors on re-deployment/restart)
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase Application Initialized successfully!");
            } else {
                 logger.info("Firebase Application already initialized.");
            }

        } catch (IOException e) {
            logger.error("Error initializing Firebase Admin SDK", e);
            // Throwing a runtime exception might be appropriate to stop app startup on critical error
            throw new RuntimeException("Could not initialize Firebase Admin SDK", e);
        }
    }

    // Example of providing the Firestore bean (your code might already have this)
    @Bean
    public Firestore getFirestore() {
        // Ensure FirebaseApp is initialized before getting Firestore client
        if (FirebaseApp.getApps().isEmpty()) {
             initializeFirebase(); // Should be initialized by @PostConstruct, but as fallback
        }
        return FirestoreClient.getFirestore();
    }

     // Example of providing the FirebaseAuth bean (if you use it for token verification)
     /*
     @Bean
     public com.google.firebase.auth.FirebaseAuth getFirebaseAuth() {
         if (FirebaseApp.getApps().isEmpty()) {
             initializeFirebase();
         }
         return com.google.firebase.auth.FirebaseAuth.getInstance();
     }
     */
}