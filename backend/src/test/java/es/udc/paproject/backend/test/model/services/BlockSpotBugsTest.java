package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import es.udc.paproject.backend.model.services.Block;

public class BlockSpotBugsTest{

    @Test 
    public void testBlockMutabilitySpotBugsBug(){
        List<String> items = new ArrayList<>();
        items.add("Item 1");

        Block<String> block = new Block<>(items, false);

        // SpotBugs alertó de que getItems() devuelve la lista interna
        // Si alguien modifica la lista que se devulve, altera el estado interno del Block
        List<String> items2 = block.getItems();
        items2.add("Item 2 (Inyección no deseada)");
        
        assertEquals(2, block.getItems().size());
    }
}
