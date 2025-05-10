package cit.edu.furrevercare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
// FileInputStream and FileNotFoundException are not directly needed for this approach
// import java.io.InputStream; // Not directly needed for this approach if getApplicationDefault handles the stream
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;

@Configuration
public class FirebaseConfig {

    // No ResourceLoader or its constructor needed for this approach

    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) { // Check if an app has already been initialized
            try {
                System.out.println("Attempting to initialize Firebase using Application Default Credentials (GOOGLE_APPLICATION_CREDENTIALS)...");
                
                // GoogleCredentials.getApplicationDefault() will automatically look for credentials,
                // including the GOOGLE_APPLICATION_CREDENTIALS environment variable you set in Render.
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault()) 
                    .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully using Application Default Credentials.");

            } catch (IOException e) {
                System.err.println("Error initializing Firebase using Application Default Credentials.");
                System.err.println("Ensure GOOGLE_APPLICATION_CREDENTIALS environment variable is set correctly in Render and points to a valid service account key file.");
                System.err.println("The value of GOOGLE_APPLICATION_CREDENTIALS is currently: " + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
                System.err.println("Detailed error: " + e.getMessage());
                e.printStackTrace(); // Print the full stack trace for more details
                throw e; // Re-throw the exception to ensure the application startup fails clearly if Firebase doesn't init
            }
        } else {
            System.out.println("Firebase Admin SDK already initialized.");
        }
    }

    @Bean
    public Firestore firestore() {
        // Ensure FirebaseApp is initialized before trying to get Firestore instance
        if (FirebaseApp.getApps().isEmpty()) {
             throw new IllegalStateException("FirebaseApp has not been initialized. Firestore bean cannot be created.");
        }
        return FirestoreClient.getFirestore();
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        // Ensure FirebaseApp is initialized
        if (FirebaseApp.getApps().isEmpty()) {
             throw new IllegalStateException("FirebaseApp has not been initialized. FirebaseAuth bean cannot be created.");
        }
        return FirebaseAuth.getInstance();
    }
}