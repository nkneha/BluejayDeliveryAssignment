import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BlueJayDeliveryAssignment {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\nehak\\OneDrive\\Desktop\\Bluejay\\Blue-Jay-Assignment\\bluejay_assignmentFile.csv"; // chnage
                                                                                                             // this
                                                                                                             // path
                                                                                                             // with
                                                                                                             // your
                                                                                                             // file
                                                                                                             // path
                                                                                                             // (take
                                                                                                             // .csv
                                                                                                             // file)

        analyzeEmployeeShifts(filePath);
    }

    public static void analyzeEmployeeShifts(String filePath) {

        Map<String, List<Shift>> employeeShifts = new HashMap<>();
        Map<String, String> employeeDetails = new HashMap<>();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 7) {
                    System.err.println("Skipping line with incomplete data: " + line);
                    continue;
                }

                String employeeName = parts[7];
                String employeePosition = parts[0];
                String startDateTimeStr = parts[2];
                String endDateTimeStr = parts[3];

                try {

                    Date startDateTime = dateTimeFormat.parse(startDateTimeStr);
                    Date endDateTime = dateTimeFormat.parse(endDateTimeStr);

                    if (!employeeShifts.containsKey(employeeName)) {

                        employeeShifts.put(employeeName, new ArrayList<>());
                        employeeDetails.put(employeeName, employeePosition);
                    }

                    List<Shift> shifts = employeeShifts.get(employeeName);

                    shifts.add(new Shift(startDateTime, endDateTime, employeePosition));

                } catch (ParseException e) {

                    System.err.println("Invalid date-time format. Skipping line: " + line);
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        for (Map.Entry<String, List<Shift>> entry : employeeShifts.entrySet()) {

            List<Shift> shifts = entry.getValue();
            int consecutiveDays = 1;
            double totalHours = 0;
            Shift previousShift = null;

            for (Shift shift : shifts) {

                if (previousShift != null) {

                    long breakDuration = shift.getStartDateTime().getTime() - previousShift.getEndDateTime().getTime();

                    totalHours += breakDuration / (1000.0 * 3600.0);

                    if (breakDuration > 1 * 3600 * 1000 && breakDuration < 10 * 3600 * 1000) {

                        System.out.println(entry.getKey() + " " + employeeDetails.get(entry.getKey())
                                + " had less than 10 hours between shifts.");
                    }
                }

                previousShift = shift;
            }

            for (int i = 1; i < shifts.size(); i++) {

                long diff = shifts.get(i).getStartDateTime().getTime() - shifts.get(i - 1).getEndDateTime().getTime();

                if (diff < 24 * 3600 * 1000) {

                    consecutiveDays++;
                } else {

                    consecutiveDays = 1;
                }

                if (consecutiveDays >= 7) {
                    System.out.println(
                            entry.getKey() + " " + employeeDetails.get(entry.getKey()) + " worked 7 consecutive days.");
                    break;
                }
            }

            for (Shift shift : shifts) {

                double shiftHours = (shift.getEndDateTime().getTime() - shift.getStartDateTime().getTime())
                        / (1000.0 * 3600.0);

                if (shiftHours > 14) {

                    System.out.println(entry.getKey() + " " + employeeDetails.get(entry.getKey())
                            + " worked for more than 14 hours in a single shift.");
                    break;
                }
            }
        }
    }

    static class Shift {
        private Date startDateTime;
        private Date endDateTime;
        private String employeePosition;

        public Shift(Date startDateTime, Date endDateTime, String employeePosition) {

            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.employeePosition = employeePosition;
        }

        public Date getStartDateTime() {
            return startDateTime;
        }

        public Date getEndDateTime() {
            return endDateTime;
        }
    }
}
