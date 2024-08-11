package MongoTable.Model;


import io.quarkus.logging.Log;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Objects;

public class LogEntry {
    private String name;
    private String description;
    private String id;

    public LogEntry(){
    }
    public LogEntry(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LogEntry)) {
            return false;
        }

        LogEntry other = (LogEntry) obj;

        return Objects.equals(other.name, this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
