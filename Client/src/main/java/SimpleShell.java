import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SimpleShell {


    public static void prettyPrint(String output) {
        // yep, make an effort to format things nicely, eh?

        ObjectMapper mapper = new ObjectMapper();

        try {
            Object json = mapper.readValue(output, Object.class);
            String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            System.out.println(pretty);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws java.io.IOException {

        YouAreEll webber = new YouAreEll();
        String commandLine;
        BufferedReader console = new BufferedReader
                (new InputStreamReader(System.in));

        ProcessBuilder pb = new ProcessBuilder();
        List<String> history = new ArrayList<String>();
        ObjectMapper mapper = new ObjectMapper();
        int index = 0;
        //we break out with <ctrl c>
        while (true) {
            //read what the user enters
            System.out.println("cmd? ");
            commandLine = console.readLine();

            //input parsed into array of strings(command and arguments)
            String[] commands = commandLine.split(" ");
            List<String> list = new ArrayList<String>();

            //if the user entered a return, just loop again
            if (commandLine.equals(""))
                continue;
            if (commandLine.equals("exit")) {
                System.out.println("bye!");
                break;
            }

            //loop through to see if parsing worked
            for (int i = 0; i < commands.length; i++) {
                //System.out.println(commands[i]); //***check to see if parsing/split worked***
                list.add(commands[i]);

            }
            System.out.print(list); //***check to see if list was added correctly***
            history.addAll(list);
            try {
                //display history of shell with index
                if (list.get(list.size() - 1).equals("history")) {
                    for (String s : history)
                        System.out.println((index++) + " " + s);
                    continue;
                }

                // Specific Commands.

                if (list.contains("ids") && list.size() > 2) {
                    String name = list.get(1);
                    String githubid = list.get(2);
                    Identifier id = new Identifier(name, githubid);
                    String json = mapper.writeValueAsString(id);
                    webber.MakeURLCall("/ids", "POST", json);
                    continue;
                }

                // ids
                if (list.contains("ids")) {
                    String results = webber.get_ids();
                    SimpleShell.prettyPrint(results);
                    continue;
                }

                if (list.contains("send")) {
                    String fromid = list.get(1);
                    StringBuilder message = new StringBuilder();
                    int indexEndMessage = 0;

                    for (int i = 3; i < list.size(); i++) {
                        if (list.get(i).contains("'")) {
                            indexEndMessage = i;
                            break;
                        }
                        indexEndMessage = 2;
                    }

                    for (int i = 2; i < indexEndMessage; i++) {
                        if (i == 2) {
                            message.append(list.get(i).substring(1));
                            message.append(" ");
                        } else {
                            message.append(list.get(i));
                            message.append(" ");
                        }
                    }

                    message.append(list.get(indexEndMessage).substring(0, list.get(indexEndMessage).length()-1));
                    String messageString = message.toString();

                    if (list.size() - 1 == indexEndMessage) {
                        Message obj = new Message(fromid, messageString);
                        String json = mapper.writeValueAsString(obj);
                        webber.MakeURLCall("/ids/" + fromid + "/messages", "POST", json);
                    } else {
                        String toid = list.get(list.size() - 1);
                        Message obj = new Message(fromid, toid, messageString);
                        String json = mapper.writeValueAsString(obj);
                        webber.MakeURLCall("/ids/" + fromid + "/messages", "POST", json);
                    }
                    continue;
                }

                // messages
                if (list.contains("messages")) {
                    if (list.size() == 1) {
                        String results = webber.get_messages();
                        SimpleShell.prettyPrint(results);
                        continue;
                    } else {
                        String urlpath = "/ids/" + list.get(1) + "/messages";
                        String results = webber.MakeURLCall(urlpath, "GET", "");
                        SimpleShell.prettyPrint(results);
                        continue;
                    }
                }
                // you need to add a bunch more.

                //!! command returns the last command in history
                if (list.get(list.size() - 1).equals("!!")) {
                    pb.command(history.get(history.size() - 2));

                }//!<integer value i> command
                else if (list.get(list.size() - 1).charAt(0) == '!') {
                    int b = Character.getNumericValue(list.get(list.size() - 1).charAt(1));
                    if (b <= history.size())//check if integer entered isn't bigger than history size
                        pb.command(history.get(b));
                } else {
                    pb.command(list);
                }

                // wait, wait, what curiousness is this?
                Process process = pb.start();

                //obtain the input stream
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                //read output of the process
                String line;
                while ((line = br.readLine()) != null)
                    System.out.println(line);
                br.close();


            }

            //catch ioexception, output appropriate message, resume waiting for input
            catch (IOException e) {
                System.out.println("Input Error, Please try again!");
            }
            // So what, do you suppose, is the meaning of this comment?
            /** The steps are:
             * 1. parse the input to obtain the command and any parameters
             * 2. create a ProcessBuilder object
             * 3. start the process
             * 4. obtain the output stream
             * 5. output the contents returned by the command
             */

        }


    }

}