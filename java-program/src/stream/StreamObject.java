package stream;

import java.util.stream.Stream;

public class StreamObject {
    public static void main(String[] args) {
        // stream API objects
        // 1-> Blank stream
        Stream<Object> emptyStream = Stream.empty();

        //2-> of method
        String[] arrays = {"1","2","3","4","5","6"};
        //Stream<String> arrays1 = Stream.of("1","2","3","4","5","6");
        Stream<String> arrays1 = Stream.of(arrays);
        arrays1.forEach(i-> {
            System.out.print(i);
        });
    }
}
