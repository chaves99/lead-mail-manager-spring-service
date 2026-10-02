package cnpj.analyzr.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cnpj.analyzr.lead.LeadRecord;

public record EmailUtils() {

    private static final String UNSUBSCRIBE_ELEMENT = """
            <a
                style="color: black; font-size: 11px; width: 100%%; display: block; text-align: center; margin-top: 10px;"
                href="https://lead-mail-manager-spring-service-production.up.railway.app/lead/unsubscribe/%d/%d">
                Cancelar inscrição
            </a>
            """;

    private static final String CAMPAIGN_ROW_ID_REPLACABLE_TOKEN = "ROW_ID";
    private static final String URL_TARGET_REPLACABLE_TOKEN = "URL_TARGET";

    private static final String CLICK_TRACKABLE_URL_PARAMS = "?campaign_row_id=" + CAMPAIGN_ROW_ID_REPLACABLE_TOKEN + "&url_target=" + URL_TARGET_REPLACABLE_TOKEN;
    private static final String CLICK_TRACKABLE_URL = "https://lead-mail-manager-spring-service-production.up.railway.app/statistics/track-click" + CLICK_TRACKABLE_URL_PARAMS;

    private static final String IMG_OPEN_EMAIL_TRACK = "<img src=\"https://lead-mail-manager-spring-service-production.up.railway.app/statistics/track-open?campaign_row_id=" + CAMPAIGN_ROW_ID_REPLACABLE_TOKEN + "\" width=\"1\" height=\"1\" alt=\"\" />";

    public static String prepareEmailBody(LeadRecord lead, Long rowId, String emailBody) {
        Document document = Jsoup.parse(emailBody);
        Element htmlElement = document.select("body").first();
        htmlElement.append(IMG_OPEN_EMAIL_TRACK.replace(CAMPAIGN_ROW_ID_REPLACABLE_TOKEN, rowId.toString()));

        findLinks(document, rowId);
        addUnsubscribeLink(document, lead.id(), rowId);
        return document.toString();
    }


    private static void addUnsubscribeLink(Document document, Long leadId, Long rowId) {
        Elements htmls = document.select("body");
        htmls.first().append(String.format(UNSUBSCRIBE_ELEMENT, leadId, rowId));
    }

    private static void findLinks(Document document, Long rowId) {
        Elements select = document.select("a[href]");
        select.forEach(e -> {
            Attribute attribute = e.attribute("href");
            String value = attribute.getValue();
            String finalTrackableUrl = CLICK_TRACKABLE_URL
                    .replace(CAMPAIGN_ROW_ID_REPLACABLE_TOKEN, rowId.toString())
                    .replace(URL_TARGET_REPLACABLE_TOKEN, value);
            e.attribute("href").setValue(finalTrackableUrl);
        });
    }
}
