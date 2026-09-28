package in.rahul.learning;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {

    public static void main(String[] args) throws InterruptedException {

        Integer aa = 100;
        Integer bb = 100;
        Integer cc = 200;

        Integer dd = 200;
        System.out.println(aa == bb);
        System.out.println(cc == dd);

        int[] arrr = {1, 0, 11, 2, 0, 2, 0, 9};
        moveZerosPlainJavaCode1(arrr);
        System.out.println(Arrays.toString(arrr));

        String str = "AABBCDA";

        StringBuilder resultSb = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            if (i == 0 || str.charAt(i) != str.charAt(i - 1)) {
                resultSb.append(str.charAt(i));
            }
        }

        System.out.println(resultSb); // ABCD

        int[] arr = {1, 2, 10, 3, 0, 1, 0, 3, 4, 0};
        int shifted = moveZerosPlainJavaCode(arr);
        System.out.println(Arrays.toString(arr));
        System.out.println(shifted);

        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> square = list.stream().map(a -> a * a).collect(Collectors.toUnmodifiableList());
        System.out.println(square);

        List<Integer> s = list.stream().map(a -> a * list.get(a - 1)).collect(Collectors.toUnmodifiableList());
        System.out.println(s);

        List<Integer> result = IntStream.range(1, list.size())
                .mapToObj(i -> list.get(i - 1) * list.get(i))
                .collect(Collectors.toList());

        System.out.println(result);

        Integer max = list.stream().sorted(Comparator.reverseOrder()).skip(1).findFirst().get();
        System.out.println(max);


        Thread thread1 = new Thread();
        Thread thread2 = new Thread();

        thread1.start();
        thread2.start();

        System.out.println("Starting TestThread");

        Thread.sleep(5000);
        thread1.join();
        thread2.join();

        System.out.println("Thread completed");


    }

    private static void moveZerosPlainJavaCode1(int[] arr) {
        int index = 0;
        int count = 0;

        for (int i : arr) {
            if (i != 0) {
                arr[index++] = i;
            } else {
                count++;
            }
        }
        while (count > 0) {
            arr[index++] = 0;
            count--;
        }

    }

    private static int moveZerosPlainJavaCode(int[] arr) {

        List<String> strings = List.of("AV", "!23", "123", "-312", "1as", "a22");
        List<Integer> printIntegers = strings.stream().map(a -> {
            try {
                return Integer.parseInt(a);
            } catch (NumberFormatException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
        ;
        System.out.println(printIntegers);

        List<Integer> print = strings.stream().filter(a -> a.matches("-?\\d+")).map(Integer::valueOf).collect(Collectors.toUnmodifiableList());
        System.out.println(print);

        int index = 0;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                arr[index++] = arr[i];
            }
        }

        System.out.println("Index " + index);

        while (index < arr.length) {
            arr[index++] = 0;
        }

        return index;

    }
}
