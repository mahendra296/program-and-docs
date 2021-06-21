package ArrayProgram;

import java.util.Arrays;

public class SecondLarge {

    public static void main(String[] args) {
        int[] array = new int []{ 10, 20, 30, 40, 60, 55, 45 };
        secLarge(array);
    }

    public static void secLarge(int arr[]) {

        int large = arr[0], sec = arr[0], third = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > large) {
                third = sec;
                sec = large;
                large = arr[i];
            } else if (arr[i] > sec) {
                third = sec;
                sec = arr[i];
            } else if (arr[i] > third) {
                third = arr[i];
            }
        }

        System.out.println("Third Large: " + third);
        System.out.println("Second Large: " + sec);
        System.out.println("Large : " + large);

    }

    public void seperate(int arr[]) {
        int c = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                arr[c] = arr[i];
                c++;
            }
        }
        while (c < arr.length) {
            arr[c] = 0;
            c++;
        }
        System.out.println(Arrays.toString(arr));
    }

    public void sorting(int arr[]) {
        int temp;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        for (int i = 0; i <= arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    public void duplicate(int arr[]) {
        try {
            System.out.println("\n");
            this.sorting(arr);
            System.out.println("\n");
            int[] temp = new int[arr.length];
            int j = 0;
            for (int i = 0; i < arr.length - 1; i++) {
                if (arr[i] != arr[i + 1]) {
                    temp[j++] = arr[i];
                }
            }

            temp[j++] = arr[arr.length - 1];
            // Changing original array
            for (int i = 0; i < j; i++) {
                arr[i] = temp[i];
                System.out.print(arr[i] + " ");
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
        }
    }
}
