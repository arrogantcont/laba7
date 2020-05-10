package commons;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.HashMap;

@Data
@AllArgsConstructor
public class User implements Serializable {
    String username;
    String password;


}
