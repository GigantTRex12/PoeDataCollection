package com.company.datasets.other.jun;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Action {

    @JsonProperty("member")
    private final Member.MemberName member;

    @JsonProperty("actionType")
    private final ActionType actionType;

    @JsonIgnore
    @Deprecated
    private Action doneAction;

    @JsonProperty("value")
    @JsonInclude(NON_NULL)
    private final Integer value;

    @JsonProperty("otherMember")
    @JsonInclude(NON_NULL)
    private final Member.MemberName otherMember;

    @JsonProperty("safehouse")
    @JsonInclude(NON_NULL)
    private final Safehouse.SafehouseType safehouse;

    public Action get() {
        if (doneAction == null) {
            return this;
        }
        return doneAction.get();
    }

    public enum ActionType {
        INTERROGATE,
        EXECUTE,
        BETRAY__INTELLIGENCE(true, false), BETRAY__RANKS(true, false),
        BETRAY__LEADER(true, false), BETRAY__REMOVE(true, false),
        BETRAY__DESTROY(true, false), BETRAY__RAISE_ALL(true, false),
        BARGAIN__INTELLIGENCE(false, true), BARGAIN__RECRUIT(false, true),
        BARGAIN__SWITCH(false, true), BARGAIN__LEAVE(false, true),
        BARGAIN__FREE_ALL(false, true), BARGAIN__DESTROY(false, true),
        BARGAIN__INTELLIGENCE_NEUTRAL(false, true), BARGAIN__REMOVE_RIVALRIES(false, true),
        BARGAIN__VEILED_ITEMS(false, true), BARGAIN__CURRENCY(false, true),
        BARGAIN__MAP(false, true), BARGAIN__UNIQUE(false, true),
        BARGAIN__SCARABS(false, true),
        RELEASE;

        private final boolean betray;
        private final boolean bargain;
        private ActionType() {
            betray = false;
            bargain = false;
        }
        private ActionType(boolean betray, boolean bargain) {
            this.betray = betray;
            this.bargain = bargain;
        }
        public boolean isBetray() { return betray; }
        public boolean isBargain() { return bargain; }
    }

}
