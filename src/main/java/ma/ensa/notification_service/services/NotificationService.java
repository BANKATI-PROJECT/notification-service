package ma.ensa.notification_service.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "${topic.credential}", groupId = "notification-group")
    public void consume(String event) {
        try {
            // Convertir l'événement JSON en objet
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode eventNode = objectMapper.readTree(event);
            String email = eventNode.get("email").asText();
            String username = eventNode.get("username").asText();
            String password = eventNode.get("password").asText();

            // Envoyer l'email
            String message = String.format(
                    "Bonjour !\n\nVotre compte a été créé avec succès. Voici vos identifiants de connexion :\n\n" +
                            "Username : %s\nMot de passe : %s\n\nMerci de garder ces informations en sécurité.",
                    username, password);
            emailService.sendEmail(email, "Création de votre compte", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

