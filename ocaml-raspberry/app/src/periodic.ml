
open Async.Std

type continuation = { f: unit -> continuation }

type t =
  {
    delay: Core.Span.t;
    continuation: continuation
  }

let delayed f = { f }

let rec loop x f () = { f = loop (f x) f }

let every delay f = { delay; continuation = delayed f }

let rec schedule_job { delay; continuation } =
  let clock_delay () = Clock.after delay in
  don't_wait_for (clock_delay () >>| fun () ->
    let next = continuation.f () in
    schedule_job { delay; continuation = next })

let schedule_jobs =
  List.iter schedule_job
