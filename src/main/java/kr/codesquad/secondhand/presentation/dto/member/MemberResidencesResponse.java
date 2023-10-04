package kr.codesquad.secondhand.presentation.dto.member;

import lombok.Getter;

import java.util.List;

@Getter
public class MemberResidencesResponse {

    private List<AddressData> addresses;

    public MemberResidencesResponse(List<AddressData> addresses) {
        this.addresses = addresses;
    }
}
