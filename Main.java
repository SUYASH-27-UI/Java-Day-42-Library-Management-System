
import java.io.*;
import java.util.*;

class Book {
    private int id;
    private String title;
    private String author;
    private int copies;

    public Book(int id, String title, String author, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = copies;
    }

    public int getId() {
        return id;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public String toCSV() {
        return id + "," + title + "," + author + "," + copies;
    }

    public void display() {
        System.out.printf(
            "ID: %d | Title: %s | Author: %s | Available: %d%n",
            id, title, author, copies
        );
    }
}

public class Main {
    private static final String FILE_NAME = "books.csv";
    private static final ArrayList<Book> books = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    private static void loadBooks() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length != 4) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(data[0].trim());
                    String title = data[1].trim();
                    String author = data[2].trim();
                    int copies = Integer.parseInt(data[3].trim());

                    if (id > 0 && copies >= 0
                            && !title.isEmpty()
                            && !author.isEmpty()
                            && findBook(id) == null) {
                        books.add(new Book(id, title, author, copies));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid record: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read books.csv: " + e.getMessage());
        }
    }

    private static boolean saveBooks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println("ID,Title,Author,Copies");

            for (Book book : books) {
                writer.println(book.toCSV());
            }

            return true;
        } catch (IOException e) {
            System.out.println("Could not save books: " + e.getMessage());
            return false;
        }
    }

    private static Book findBook(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }

        return null;
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static void addBook() {
        int id = readInt("Enter book ID: ");

        if (id <= 0 || findBook(id) != null) {
            System.out.println("ID must be positive and unique.");
            return;
        }

        String title = readText("Enter book title: ");
        String author = readText("Enter author name: ");
        int copies = readInt("Enter number of copies: ");

        if (title.isEmpty() || author.isEmpty() || copies < 0) {
            System.out.println("Invalid book details.");
            return;
        }

        books.add(new Book(id, title, author, copies));

        if (saveBooks()) {
            System.out.println("Book added successfully!");
        } else {
            books.remove(books.size() - 1);
            System.out.println("Book was not saved.");
        }
    }

    private static void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        System.out.println("\n===== LIBRARY BOOKS =====");

        for (Book book : books) {
            book.display();
        }
    }

    private static void searchBook() {
        int id = readInt("Enter book ID to search: ");
        Book book = findBook(id);

        if (book == null) {
            System.out.println("Book not found.");
        } else {
            book.display();
        }
    }

    private static void changeCopies(boolean issuing) {
        int id = readInt("Enter book ID: ");
        Book book = findBook(id);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        int oldCopies = book.getCopies();

        if (issuing) {
            if (oldCopies == 0) {
                System.out.println("No copies available to issue.");
                return;
            }

            book.setCopies(oldCopies - 1);
        } else {
            book.setCopies(oldCopies + 1);
        }

        if (saveBooks()) {
            System.out.println(issuing
                    ? "Book issued successfully!"
                    : "Book returned successfully!");
            System.out.println("Available copies: " + book.getCopies());
        } else {
            book.setCopies(oldCopies);
            System.out.println("Operation cancelled because saving failed.");
        }
    }

    public static void main(String[] args) {
        loadBooks();

        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT =====");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Exit");

            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    searchBook();
                    break;
                case 4:
                    changeCopies(true);
                    break;
                case 5:
                    changeCopies(false);
                    break;
                case 6:
                    System.out.println("Library system closed.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
