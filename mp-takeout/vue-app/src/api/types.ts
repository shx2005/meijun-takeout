export interface AuthLoginDTO {
  username: string;
  password: string;
}

export interface AuthLoginVo {
  id: number;
  username: string;
  name: string;
  uuid: string;
  token: string;
}

export interface KaptchaVO {
  uuid: string;
  code: string;
}
