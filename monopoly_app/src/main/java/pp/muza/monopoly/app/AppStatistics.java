package pp.muza.monopoly.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pp.muza.monopoly.data.TurnInfo;

import java.util.ArrayList;
import java.util.List;

public final class AppStatistics implements StatCollector {

    final List<TurnInfo> turns = new ArrayList<>();

    @Override
    public void collect(TurnInfo turn) {
        turns.add(turn);
    }

    @Override
    public String toString() {
        String result;
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        try {
            result = mapper.writeValueAsString(turns);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


}
