public class FindTotalDuplicateValueWIthoutAnyStreamFunction {
    public static void main(String[] args) {
        findDuplicateValues();
    }

    private static void findDuplicateValues() {
        int arr[] = {10, 12, 12, 10, 10, 15, 13, 12, 10};
        int arr1[] = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            var count = 1;
            if (isPresent(arr1, arr[i])) {
                continue;
            }
            arr1[i] = arr[i];
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] == arr[j]) {
                    count++;
                }
            }
            System.out.println(arr[i] + " => " + count);
        }
    }

    private static boolean isPresent(int[] arr1, int value) {
        for (int i = 0; i < arr1.length; i++) {
            if (value == arr1[i]) {
                return true;
            }
        }
        return false;
    }
}