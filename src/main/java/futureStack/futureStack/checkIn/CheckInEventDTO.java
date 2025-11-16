package futureStack.futureStack.checkIn;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CheckInEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long userId;
    private Long checkInId;
    private Integer score;
    private String date;
}