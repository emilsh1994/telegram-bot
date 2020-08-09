package ru.ufanet;

public class Note {

    public int noteId;
    public String cmdText;
    public String noteText;

    public Note() {
    }

    public Note(int noteId, String cmdText, String noteText) {
        this.noteId = noteId;
        this.cmdText = cmdText;
        this.noteText = noteText;
    }

    public String getCmdText() {
        return cmdText;
    }

    public void setCmdText(String cmdText) {
        this.cmdText = cmdText;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
}
