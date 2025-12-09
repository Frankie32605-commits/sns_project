import java.util.*;
public class Sorts {
    //Heap Sort Implementation
    public static <T> void heapSort(List<T> list, Comparator<? super T> comparator) {
        int n = list.size();

        //Build (max) heap (rearrange array)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i, comparator);
        }

        //One by one extract an element from heap
        for (int i = n - 1; i > 0; i--) {
            //Move current root to end
            Collections.swap(list, 0, i);

            //call max heapify on the reduced heap
            heapify(list, i, 0, comparator);
        }
    }

    private static <T> void heapify(List<T> list, int n, int i, Comparator<? super T> comparator) {
        int largest = i; // Initialize largest as root
        int left = 2 * i + 1; // left = 2*i + 1
        int right = 2 * i + 2; // right = 2*i + 2

        // If left child is larger than root
        if (left < n && comparator.compare(list.get(left), list.get(largest)) > 0) {
            largest = left;
        }

        // If right child is larger than largest so far
        if (right < n && comparator.compare(list.get(right), list.get(largest)) > 0) {
            largest = right;
        }

        // If largest is not root
        if (largest != i) {
            Collections.swap(list, i, largest);

            // Recursively heapify the affected sub-tree
            heapify(list, n, largest, comparator);
        }
    }
}