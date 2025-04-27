package com.example.rpms.model;

public class ReminderService {
    private Notifiable notifier;

    public ReminderService(Notifiable notifier) {
        this.notifier = notifier;
    }
    public void sendAppointmentReminder(String patient,String time){
        System.out.println("Sending appointment reminder for " + patient + " at " + time);
        notifier.sendNotification("Appointment reminder for " + patient + " at " + time, patient);
        System.out.println("Reminder sent to " + patient);
    }
    public void sendMedicationReminder(String patient,String medication){
        System.out.println("Sending medication reminder for " + patient + " to take " + medication);
        notifier.sendNotification("Medication reminder for " + patient + " to take " + medication, patient);
        System.out.println("Reminder sent to " + patient);
    }
}
