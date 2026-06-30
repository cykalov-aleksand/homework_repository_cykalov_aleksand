
import org.example.HelloOtus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloOtusTest {
    static Logger logger= LoggerFactory.getLogger(HelloOtusTest.class);
    @Test
    public void demonstrationNull(){
       HelloOtus helloOtus=new HelloOtus();
        String result=helloOtus.demonstration();
        info(helloOtus.getElement());
        Assertions.assertEquals("Привет Otus!", result);
    }
    @Test
    public void demonstrationEmpty(){
        HelloOtus helloOtus=new HelloOtus();
        helloOtus.setElement("");
        info(helloOtus.getElement());
        String result=helloOtus.demonstration();
        Assertions.assertEquals("Привет Otus!", result);
    }
    @Test
    public void demonstrationElement(){
        HelloOtus helloOtus=new HelloOtus();
        helloOtus.setElement("Hello Otus!");
        info(helloOtus.getElement());
        String result=helloOtus.demonstration();
        Assertions.assertEquals(helloOtus.getElement(), result);
    }
    private void info(String string){
        logger.info("Тестирование метода при вводе поля edit= {}",string);
         }
    }
