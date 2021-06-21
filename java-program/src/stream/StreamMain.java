package stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamMain {
    public static void main(String[] args) {
        List<Integer> list1 = Arrays.asList(1, 2, 13, 12, 14, 15, 18, 20);

        List<Integer> evenList = new ArrayList<>();

        // find even number without stream API
        for (Integer num : list1) {
            if (num % 2 == 0) {
                evenList.add(num);
            }
        }
        System.out.println("******Without stream*******");
        System.out.println("Even List : " + evenList);

        System.out.println("******Using Stream*******");
        List<Integer> evenCollect = list1.stream().filter(t -> t % 2 == 0).collect(Collectors.toList());
        System.out.println("Even List : " + evenCollect);
    }
}
