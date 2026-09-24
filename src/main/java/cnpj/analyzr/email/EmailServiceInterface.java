package cnpj.analyzr.email;

public interface EmailServiceInterface {

    public EmailResult send(String customerEmail, String subject, String body);

    public EmailResult sendToOwner(String subject, String body);

    public static record EmailResult(String message, boolean success, String externalId) {
        public EmailResult(Object message, boolean success, Object externalId) {
            this(message != null ? message.toString() : null, success,
                    externalId != null ? externalId.toString() : null);
        }
    }
}
