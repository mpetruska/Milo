
type t

val connect: host:string -> port:int -> t

val send: t -> bytes -> t
