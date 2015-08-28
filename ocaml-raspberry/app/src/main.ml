
open Core.Std

let delay = Core.Span.of_sec 1.0

(*
let schedule_jobs () =
  Periodic.(
    [ every (Core.Span.of_sec 0.1) (loop 1 (fun i -> print_endline (Printf.sprintf "A [%d] - %.0f" i (Unix.time ())); i + 1));
      every (Core.Span.of_sec 1.0) (loop 1 (fun i -> print_endline (Printf.sprintf "%d sec!" i); i + 1))
    ]
    |> schedule_jobs)
*)

let data i =
  (Printf.sprintf "A [%d] - %.0f" i (Unix.time ()))

let schedule_jobs host port () =
  let connection = Tcp_client.connect ~host ~port in
  Periodic.(
    every delay (loop (1, connection) (fun (i, connection) ->
                                           (i + 1, Tcp_client.send connection (data i))))
    |> schedule_job
  )

let main host port () =
  Async.Std.Scheduler.go_main ~main:(schedule_jobs host port) () |> Core.Std.never_returns

let command =
  Command.basic
    ~summary: "Sends measurements periodically to a TCP server"
    Command.Spec.(
      empty
      +> flag "-h" (required string) ~doc: "string The host to connect to"
      +> flag "-p" (required int) ~doc: "int The port to connect to")
    main

let () =
  Command.run ~version: "0.1" command 
