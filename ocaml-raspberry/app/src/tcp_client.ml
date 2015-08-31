
open Async.Std

type connection_parameters =
  { host: string;
    port: int }

type connection_status =
  | Disconnected
  | Connected of (Reader.t * Writer.t)

type connection =
  { parameters: connection_parameters;
    status: connection_status;
    mutable is_connected: bool }

type t = connection Deferred.t

let string_of_parameters parameters =
  Printf.sprintf "%s:%d" parameters.host parameters.port

let address_of_parameters parameters =
  Tcp.to_host_and_port parameters.host parameters.port

let connect_to_server connection =
  match connection.status with
  | Connected _ when connection.is_connected -> return connection
  | _ ->
      try_with (fun () -> Tcp.connect (address_of_parameters connection.parameters))
      >>| function
          | Core.Std.Ok (_, r, w) ->
              let connection = { connection with status = Connected (r, w); is_connected = true } in
              upon (w |> Writer.monitor |> Monitor.get_next_error) (fun _ -> print_endline "disconnected"; connection.is_connected <- false);
              connection
          | Core.Std.Error _      ->
              print_endline (Printf.sprintf "Could not connect to %s" (string_of_parameters connection.parameters));
              connection

let send_to_server bytes connection =
  match connection.status with
  | Connected (_, w) when connection.is_connected ->
      Writer.write w bytes |> ignore;
      return connection
  | _ ->
      return connection

let connect ~host ~port =
  { parameters = { host; port };
    status = Disconnected;
    is_connected = false }
  |> return
  >>= connect_to_server

let send connection bytes =
  connection
  >>= connect_to_server
  >>= send_to_server bytes
