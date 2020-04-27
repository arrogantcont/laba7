package server.lab6;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<K, V> {
    K first;
    V second;
}
