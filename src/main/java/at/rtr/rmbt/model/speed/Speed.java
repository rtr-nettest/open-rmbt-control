package at.rtr.rmbt.model.speed;

import at.rtr.rmbt.enums.SpeedDirection;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "speed")
@EqualsAndHashCode
public class Speed implements Serializable {

    @Id
    private UUID openTestUuid;

    @Column(name = "items", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<SpeedDirection, Map<Long, List<SpeedItem>>> items = new HashMap<>();

    public void addSpeedItem(SpeedItem speedItem, SpeedDirection speedDirection, Long thread) {
        Map<Long, List<SpeedItem>> directionMap = Optional.ofNullable(items.get(speedDirection))
                .orElseGet(() -> {
                    Map<Long, List<SpeedItem>> newDirectionMap = new HashMap<>();
                    items.put(speedDirection, newDirectionMap);
                    return newDirectionMap;
                });

        List<SpeedItem> threadList = Optional.ofNullable(directionMap.get(thread))
                .orElseGet(() -> {
                    List<SpeedItem> newThreadList = new ArrayList<>();
                    directionMap.put(thread, newThreadList);
                    return newThreadList;
                });

        threadList.add(speedItem);
    }
}
