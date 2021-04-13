package codeGenerationTest;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
//import org.junit.jupiter.api.Test;

public class MoonTest {



    @Test
    public void executeSimpleMoonProgram_withPutStatements_getPrintedResults() {
        String moonFile = "src/test/codeGenerationTest/simple.m";

        CodeGenHelper.MoonResult result = CodeGenHelper.executeMoonProgram(moonFile);

        List<String> output = result.getUsefulOutput();
        System.out.println(result);
        assertEquals(4, output.size());
        assertEquals("Z", output.get(0));
        assertEquals("a", output.get(1));
        assertEquals("c", output.get(2));
        assertEquals("h", output.get(3));
    }


    @Ignore
    @Test
    public void executeSimpleMainMoonProgram() {
        String moonFile = "src/test/codeGenerationTest/simplemain.m src/test/codeGenerationTest/lib.m";
        CodeGenHelper.InputAsker asker = mock(CodeGenHelper.InputAsker.class);
        when(asker.ask(anyString())).thenReturn("3");

        CodeGenHelper.MoonResult result = CodeGenHelper.executeMoonProgram(moonFile);


        List<String> output = result.getUsefulOutput();
        System.out.println(result);
        assertEquals(4, output.size());
        assertEquals("Z", output.get(0));
        assertEquals("a", output.get(1));
        assertEquals("c", output.get(2));
        assertEquals("h", output.get(3));
    }




}
