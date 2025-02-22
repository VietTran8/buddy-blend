import { Group, UserDetails } from "."

export type ProfileOutletContextType = {
    user?: UserDetails,
    isFetching: boolean
}

export type OutletGroupContextType = {
    group?: Group
}