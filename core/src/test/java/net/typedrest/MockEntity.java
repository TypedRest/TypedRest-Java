package net.typedrest;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MockEntity {

    private long id;

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
