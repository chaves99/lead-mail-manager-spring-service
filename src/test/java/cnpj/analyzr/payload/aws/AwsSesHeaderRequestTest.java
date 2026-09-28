package cnpj.analyzr.payload.aws;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AwsSesHeaderRequestTest {

    @Test
    public void test () {
        String expected = "campaignRowId=123";
        
        AwsSesHeaderRequest header = new AwsSesHeaderRequest(123l);

        assertEquals(expected, header.toString());
        assertEquals(header, AwsSesHeaderRequest.fromHeader(expected).get());
    }
}
