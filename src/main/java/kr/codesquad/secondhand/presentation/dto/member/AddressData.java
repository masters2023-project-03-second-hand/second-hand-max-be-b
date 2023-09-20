package kr.codesquad.secondhand.presentation.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressData {

    private Long addressId;
    private String fullAddressName;
    private String addressName;
    private boolean isSelected;
}
