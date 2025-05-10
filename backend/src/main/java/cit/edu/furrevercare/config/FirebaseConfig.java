package cit.edu.furrevercare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
public class FirebaseConfig {

    // ResourceLoader might not be needed anymore if you only used it for this
    // private final ResourceLoader resourceLoader;
    // public FirebaseConfig(ResourceLoader resourceLoader) {
    //    this.resourceLoader = resourceLoader;
    // }

    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            // Replace "classpath:serviceAccountKey.json" with the actual path from Render
            String firebaseSecretFilePath = "/etc/secrets/serviceAccountKey.json"; // <--- UPDATE THIS PATH

            // Check if the path is being overridden by an environment variable (good practice)
            String pathFromEnv = System.getenv("FIREBASE_KEY_PATH");
            if (pathFromEnv != null && !pathFromEnv.isEmpty()) {
                firebaseSecretFilePath = pathFromEnv;
            }

            System.out.println("Attempting to load Firebase service account key from: " + firebaseSecretFilePath);

            try (InputStream serviceAccount = new FileInputStream(firebaseSecretFilePath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully.");
            } catch (FileNotFoundException e) {
                System.err.println("Firebase service account key file not found at: " + firebaseSecretFilePath + ". Error: " + e.getMessage());
                throw e;
            } catch (IOException e) {
                System.err.println("Error initializing Firebase Admin SDK with file: " + firebaseSecretFilePath + ". Error: " + e.getMessage());
                throw e;
            }
        }
    }

    // ... your @Bean methods for Firestore and FirebaseAuth remain the same ...
    @Bean
    public Firestore firestore() {
        // Ensure FirebaseApp is initialized before calling this
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