
type continuation

type t

val delayed: (unit -> continuation) -> continuation

val loop: 'a -> ('a -> 'a) -> unit -> continuation

val every: Core.Span.t -> (unit -> continuation) -> t

val schedule_job: t -> unit

val schedule_jobs: t list -> unit
