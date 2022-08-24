import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Duke {
    private static ArrayList<Task> tasks;
    private static File dukeFile = new File("data/duke.txt");

    public static void tedResponse(String filler) {
        System.out.println("~ |._.| ~\n" + filler + "~\n");
    }

    public static void writeToFile() {
        try {
            FileWriter fw = new FileWriter("data/duke.txt");
            for (int i = 0; i < tasks.size(); i++) {
                fw.write(tasks.get(i).toFileString());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<Task> loadFromFile() {
        ArrayList<Task> temp = new ArrayList<>();
        Scanner sc;

        if (dukeFile.exists()) {
            try {
                sc = new Scanner(dukeFile);

                while (sc.hasNextLine()) {
                    String[] st = sc.nextLine().split(" \\| ");
                    boolean isTaskDone;

                    if (st[1].equals("1")) {
                        isTaskDone = true;
                    } else {
                        isTaskDone = false;
                    }

                    switch (st[0]) {
                        case "T":
                            temp.add(new Todo(st[2], isTaskDone));
                            break;
                        case "D":
                            temp.add(new Deadline(st[2], isTaskDone, st[3]));
                            break;
                        case "E":
                            temp.add(new Event(st[2], isTaskDone, st[3]));
                            break;
                        default:
                            break;

                    }
                }
                sc.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            try {
                File dataFolder = new File("data");
                dataFolder.mkdirs();
                dukeFile.createNewFile();
            } catch (SecurityException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return temp;
    }

    public static void main(String[] args) {
        String banner = "~~~~~~~~~~~\n"
                + " TED |._.|\n"
                + "~~~~~~~~~~~\n";
        System.out.println(banner);
        System.out.println("Hello! I'm Ted and I'm here to help you keep track of your tasks |._.|\n"
                + "How can I assist you today?\n");

        tasks = loadFromFile();

        Scanner sc = new Scanner(System.in).useDelimiter("\\n");

        while (sc.hasNext()) {
            String command = sc.next();

            String[] temp = command.split(" ", 2);
            String action = temp[0];

            if (temp.length == 1 && action.equals("bye")) {
                System.out.println("Goodbye! Have a pleasant day |._.|");
                sc.close();
                return;
            }
            if (temp.length == 1 && action.equals("list")) {
                String listOfTasks = "Your tasklist:\n";
                for (int i = 0; i < tasks.size(); i++) {
                    int bulletPoint = i + 1;
                    listOfTasks = listOfTasks + bulletPoint + ". " + tasks.get(i) + "\n";
                }
                tedResponse(listOfTasks);
                continue;
            }

            try {
                if (action.equals("mark")) {
                    if (temp.length == 1) {
                        throw new DukeException("Oh no, please indicate task to mark T_T\n");
                    }
                    String elaboration = temp[1];
                    int currTaskNumber = Integer.parseInt(elaboration);
                    Task currTask = tasks.get(currTaskNumber - 1);
                    currTask.markDone();
                    tedResponse("Great! Task done:\n" + currTask + "\n");
                    writeToFile();

                } else if (action.equals("unmark")) {
                    if (temp.length == 1) {
                        throw new DukeException("Oh no, please indicate task to unmark T_T\n");
                    }
                    String elaboration = temp[1];
                    int currTaskNumber = Integer.parseInt(elaboration);
                    Task currTask = tasks.get(currTaskNumber - 1);
                    currTask.unmarkDone();
                    tedResponse("Aw :( Task undone:\n" + currTask + "\n");
                    writeToFile();

                } else if (action.equals("delete")) {
                    if (temp.length == 1) {
                        throw new DukeException("Oh no, please indicate task to delete T_T\n");
                    }
                    String elaboration = temp[1];
                    int currTaskNumber = Integer.parseInt(elaboration);
                    Task currTask = tasks.get(currTaskNumber - 1);
                    tasks.remove(currTaskNumber - 1);
                    tedResponse("Done! Task deleted:\n" + currTask + "\nremaining task count: "
                            + tasks.size() + "\n");
                    writeToFile();

                } else if (action.equals("todo")) {
                    if (temp.length == 1) {
                        throw new DukeException("Oh no, please include task description T_T\n");
                    }
                    String elaboration = temp[1];
                    Task currTask = new Todo(elaboration);
                    tasks.add(currTask);
                    tedResponse("added to tasklist:\n" + currTask + "\ntask count: "
                            + tasks.size() + "\n");
                    writeToFile();

                } else if (action.equals("deadline")) {
                    if (temp.length == 1) {
                        throw new DukeException("Oh no, please include task description T_T\n");
                    }
                    String elaboration = temp[1];
                    String[] currTaskDesc = elaboration.split(" /by ", 2);
                    if (currTaskDesc.length == 1) {
                        throw new DukeException("Oh no, please include both deadline description and time T_T\n");
                    }
                    Task currTask = new Deadline(currTaskDesc[0], currTaskDesc[1]);
                    tasks.add(currTask);
                    tedResponse("added to tasklist:\n" + currTask + "\ntask count: "
                            + tasks.size() + "\n");
                    writeToFile();

                } else if (action.equals("event")) {
                    if (temp.length == 1) {
                        throw new DukeException("Oh no, please include task description T_T\n");
                    }
                    String elaboration = temp[1];
                    String[] currTaskDesc = elaboration.split(" /at ", 2);
                    if (currTaskDesc.length == 1) {
                        throw new DukeException("Oh no, please include both event description and time T_T\n");
                    }
                    Task currTask = new Event(currTaskDesc[0], currTaskDesc[1]);
                    tasks.add(currTask);
                    tedResponse("added to tasklist:\n" + currTask + "\ntask count: "
                            + tasks.size() + "\n");
                    writeToFile();

                } else {
                    throw new DukeException("Oh no, I don't understand T_T\n");
                }
            } catch (DukeException e) {
                tedResponse(e.getMessage());
            } catch (NumberFormatException e) {
                tedResponse("Oh no, please indicate task to mark/unmark/delete with a number T_T\n");
            } catch (IndexOutOfBoundsException e) {
                tedResponse("Oh no, there's no such task T_T\n");
            }
        }
    }
}
