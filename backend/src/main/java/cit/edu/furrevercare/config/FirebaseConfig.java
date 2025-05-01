package cit.edu.furrevercare.config; // Ensure this package matches your project structure

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth; // <-- Import FirebaseAuth
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct; // Use jakarta for Spring Boot 3+
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    // This method runs after the bean is created and initializes Firebase
    @PostConstruct
    public void initializeFirebase() {
        try {
            // Let the SDK automatically find credentials based on the environment
            // (It checks GOOGLE_APPLICATION_CREDENTIALS environment variable first)
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                // Explicitly set Project ID (from your service account JSON)
                .setProjectId("furrevercare-fe125")
                .build();

            // Initialize only if no default app exists yet
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase Application Initialized successfully!");
            } else {
                 logger.info("Firebase Application already initialized.");
            }

        } catch (IOException e) {
            logger.error("Error initializing Firebase Admin SDK", e);
            // Stop application startup if Firebase initialization fails
            throw new RuntimeException("Could not initialize Firebase Admin SDK", e);
        }
    }

    // This makes the Firestore client available as a Spring Bean
    @Bean
    public Firestore getFirestore() {
        // Ensure FirebaseApp is initialized first
        if (FirebaseApp.getApps().isEmpty()) {
             logger.warn("FirebaseApp was not initialized before getFirestore bean creation. Attempting init.");
             initializeFirebase();
        }
        return FirestoreClient.getFirestore();
    }

    // This makes the FirebaseAuth service available as a Spring Bean
    @Bean
    public FirebaseAuth getFirebaseAuth() {
        // Ensure FirebaseApp is initialized first
        if (FirebaseApp.getApps().isEmpty()) {
             logger.warn("FirebaseApp was not initialized before getFirebaseAuth bean creation. Attempting init.");
             initializeFirebase();
        }
        return FirebaseAuth.getInstance();
    }
}